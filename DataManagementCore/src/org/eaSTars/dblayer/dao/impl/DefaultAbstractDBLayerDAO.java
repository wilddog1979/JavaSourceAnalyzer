package org.eaSTars.dblayer.dao.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eaSTars.dblayer.dao.AbstractDBLayerDAO;
import org.eaSTars.dblayer.dao.DatabaseConnectionException;
import org.eaSTars.dblayer.dao.FilterEntry;
import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.ForeignKey;
import org.eaSTars.dblayer.model.GenericModel;

public abstract class DefaultAbstractDBLayerDAO implements AbstractDBLayerDAO {

	private static final Logger LOGGER = LogManager.getLogger(DefaultAbstractDBLayerDAO.class);
	
	private DataSource datasource;
	
	public <T extends GenericModel> T getModelByPK(Class<T> modelclass, Integer id) {
		return queryModel(modelclass, new FilterEntry("PK", id));
	}
	
	private List<Field> getFields(Class<?> modelclass) {
		List<Field> result = Arrays.asList(
				Optional.ofNullable(modelclass.getDeclaredFields())
				.orElse(new Field[]{})).stream()
		.filter(field -> field.isAnnotationPresent(Attribute.class))
		.collect(Collectors.toList());
		
		if (GenericModel.class.isAssignableFrom(modelclass)) {
			List<Field> temp = getFields(modelclass.getSuperclass());
			temp.addAll(result);
			result = temp;
		}
		
		return result;
	}
	
	private Field getField(Class<?> modelclass, String name) {
		return Arrays.asList(
				Optional.ofNullable(modelclass.getDeclaredFields())
				.orElse(new Field[]{})).stream()
		.filter(field -> field.isAnnotationPresent(Attribute.class) && field.getName().equals(name))
		.findFirst().orElseGet(() -> {
			if (GenericModel.class.isAssignableFrom(modelclass)) {
				return getField(modelclass.getSuperclass(), name);
			} else {
				return null;
			}
		});
	}
	
	private Deployment getDeploymentTag(Class<?> modelclass) {
		Deployment deployment = modelclass.getAnnotation(Deployment.class);
		
		if (deployment == null && GenericModel.class.isAssignableFrom(modelclass)) {
			deployment = getDeploymentTag(modelclass.getSuperclass());
		}
		
		return deployment;
	}
	
	private void addParameterValues(PreparedStatement preparedStatement, List<ParameterEntry> parameterValues, boolean nullrequired) throws SQLException {
		int paramcount = 1;
		
		for (ParameterEntry parameter : parameterValues) {
			if (parameter.value != null || nullrequired) {
				if (parameter.type.isAssignableFrom(String.class)) {
					if (parameter.value == null) {
						preparedStatement.setNull(paramcount, java.sql.Types.VARCHAR);
						LOGGER.trace("Parameter: (String) NULL");
					} else {
						preparedStatement.setString(paramcount, (String) parameter.value);
						LOGGER.trace("Parameter: (String) {}", (String) parameter.value);
					}
				} else if (parameter.type.isAssignableFrom(Integer.class) || parameter.type.isAssignableFrom(int.class)) {
					if (parameter.value == null) {
						preparedStatement.setNull(paramcount, java.sql.Types.INTEGER);
						LOGGER.trace("Parameter: (Integer) NULL");
					} else {
						preparedStatement.setInt(paramcount, (Integer) parameter.value);
						LOGGER.trace("Parameter: (Integer) {}", (Integer) parameter.value);
					}
				} else if (parameter.type.isAssignableFrom(Boolean.class)) {
					if (parameter.value == null) {
						preparedStatement.setNull(paramcount, java.sql.Types.BIT);
						LOGGER.trace("Parameter: (Boolean) NULL");
					} else {
						preparedStatement.setBoolean(paramcount, (Boolean) parameter.value);
						LOGGER.trace("Parameter: (Boolean) {}", (Boolean) parameter.value);
					}
				} else {
					System.out.println("Unknown : "+parameter);
				}
				paramcount++;
			}
		}
	}
	
	private static class ParameterEntry {
		Field property;
		String columnname;
		Class<?> type;
		Object value;
		
		ForeignKey fk[];
		
		public ParameterEntry(Field property, String columnname, Class<?> type, Object value) {
			this.property = property;
			this.columnname = columnname;
			this.type = type;
			this.value = value;
		}
	}
	
	protected Optional<Method> findMethod(Class<?> clazz, String setget, String name) {
		return Arrays.asList(clazz.getMethods()).stream()
				.filter(method -> method.getName().equalsIgnoreCase(setget + name))
				.findFirst();
	}
	
	private Map<Class<?>, PreparedStatement> insertCache = new HashMap<>();
	
	private Map<Class<?>, PreparedStatement> updateCache = new HashMap<>();
	
	@Override
	public <T extends GenericModel> void saveModel(T model) {
		Deployment deployment = getDeploymentTag(model.getClass());
		if (deployment != null) {
			ParameterEntry[] pk = {null};
			List<ParameterEntry> entries = getFields(model.getClass()).stream()
					.map(field -> Optional.ofNullable(field.getAnnotation(Attribute.class))
							.map(attribute -> Optional.ofNullable(findMethod(model.getClass(), "get", field.getName())
									.map(method -> {
										try {
											Object[] parameters = {};
											ParameterEntry pe = new ParameterEntry(field, attribute.column(), method.getReturnType(), method.invoke(model, parameters));
											if (attribute.primarykey()) {
												pk[0] = pe;
												return null;
											} else {
												return pe;
											}
										} catch (IllegalAccessException | IllegalArgumentException
												| InvocationTargetException e) {
											throw new DatabaseConnectionException(e);
										}})
									.orElse(null))
									.orElse(null))
							.orElse(null))
					.filter(Objects::nonNull)
					.collect(Collectors.toList());

			try {
				PreparedStatement pstatement = null;
				if (model.getPK() == null) {
					pstatement = insertCache.get(model.getClass());
					if (pstatement == null) {
						List<ParameterEntry> tempentries = new ArrayList<>();

						String query = String.format("INSERT INTO %s (%s) VALUES (%s);", deployment.table(),
								entries.stream()
								.filter(entry -> entry.type != null)
								.map(entry -> {
									tempentries.add(entry);
									return entry.columnname;
								})
								.collect(Collectors.joining(", ")),
								tempentries.stream()
								.map(o -> "?")
								.collect(Collectors.joining(", ")));
						entries = tempentries;

						LOGGER.trace(query);

						pstatement = datasource.getConnection().prepareStatement(
								query, PreparedStatement.RETURN_GENERATED_KEYS);
						insertCache.put(model.getClass(), pstatement);
					}
				} else {
					pstatement = updateCache.get(model.getClass());
					if (pstatement == null) {
						List<ParameterEntry> tempentries = new ArrayList<>();

						String query = String.format("UPDATE %s SET %s WHERE PK = ?;", deployment.table(),
								entries.stream()
								.filter(entry -> entry.type != null)
								.map(entry -> {
									tempentries.add(entry);
									return entry.columnname+" = ?";
								})
								.collect(Collectors.joining(", ")));
						entries = tempentries;

						LOGGER.trace(query);

						pstatement = datasource.getConnection().prepareStatement(
								query, PreparedStatement.RETURN_GENERATED_KEYS);
						updateCache.put(model.getClass(), pstatement);
					}
					entries.add(pk[0]);
				}
				if (pstatement != null) {
					addParameterValues(pstatement, entries, true);
					pstatement.executeUpdate();

					ResultSet rs = pstatement.getGeneratedKeys();
					if (rs != null && rs.next() && rs.getInt(1) != 0) {
						model.setPK(rs.getInt(1));
					}
				}
			} catch (SQLException e) {
				throw new DatabaseConnectionException(e);
			}
		} else {
			throw new DatabaseConnectionException("Model deployment annotation not found");
		}
	}
	
	private class QueryCharacteristic {
		private String name;
		
		private boolean isnull;
		
		public QueryCharacteristic(String name, boolean isnull) {
			this.name = name;
			this.isnull = isnull;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (isnull ? 1231 : 1237);
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof QueryCharacteristic &&
					((QueryCharacteristic)obj).name.equals(name) &&
					((QueryCharacteristic)obj).isnull == isnull;
		}

		private DefaultAbstractDBLayerDAO getOuterType() {
			return DefaultAbstractDBLayerDAO.this;
		}
	}
	
	private Map<Class<?>, Map<List<QueryCharacteristic>, PreparedStatement>> querycache = new HashMap<>();
	
	public <T extends GenericModel> ResultSet queryModelCommon(Class<T> modelclass, FilterEntry... parameters) {
		Deployment deployment = getDeploymentTag(modelclass);
		if (deployment != null) {
			List<QueryCharacteristic> currentcharacteristic = Arrays.asList(parameters).stream()
					.map(parameter -> new QueryCharacteristic(parameter.getPropertyName(), parameter.getValue() == null))
					.collect(Collectors.toList());

			Map<List<QueryCharacteristic>, PreparedStatement> modellevel = querycache.get(modelclass);

			PreparedStatement pstatement = null;
			if (modellevel != null) {
				pstatement = modellevel.entrySet().stream()
				.filter(e -> e.getKey().equals(currentcharacteristic)).findFirst()
				.map(Entry<List<QueryCharacteristic>, PreparedStatement>::getValue).orElseGet(() -> null);
			} else {
				modellevel = new HashMap<>();
				querycache.put(modelclass, modellevel);
			}

			List<ParameterEntry> filters = Arrays.asList(parameters).stream()
					.map(parameter -> Optional.ofNullable(getField(modelclass, parameter.getPropertyName()))
							.map(field -> Optional.ofNullable(field.getAnnotation(Attribute.class))
									.map(attribute -> new ParameterEntry(field, attribute.column(), field.getType(), parameter.getValue()))
									.orElse(null))
							.orElse(null))
					.filter(Objects::nonNull)
					.collect(Collectors.toList());

			try {
				if (pstatement == null) {

					String projection = getFields(modelclass).stream()
							.filter(field -> findMethod(modelclass, "set", field.getName()).isPresent())
							.map(field -> Optional.ofNullable(field.getAnnotation(Attribute.class)))
							.filter(Optional<Attribute>::isPresent)
							.map(attribute -> attribute.get().column())
							.collect(Collectors.joining(", "));

					String whereclause = filters.stream()
							.map(filter -> {
								if (filter.value == null) {
									return filter.columnname + " IS NULL";
								} else {
									return filter.columnname + " = ?";
								}
							})
							.collect(Collectors.joining(" AND "));

					String query = String.format("SELECT %s FROM %s WHERE %s;", projection, deployment.table(), whereclause);

					LOGGER.trace(query);

					pstatement = datasource.getConnection().prepareStatement(query);
					modellevel.put(currentcharacteristic, pstatement);

				}

				addParameterValues(pstatement, filters, false);
				return pstatement.executeQuery();
			} catch (SQLException e) {
				throw new DatabaseConnectionException(e);
			}
		} else {
			throw new DatabaseConnectionException("Model deployment annotation not found");
		}
	}
	
	protected <T extends GenericModel> T extractEntry(Class<T> modelclass, ResultSet rs) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		Class<?>[] constructorParameterTypes = {};
		Constructor<?> constructor = modelclass.getConstructor(constructorParameterTypes);
		Object[] constructorParameters = {};
		final GenericModel model = modelclass.cast(constructor.newInstance(constructorParameters));
		
		getFields(modelclass).stream()
		.forEach(field -> Optional.ofNullable(field.getAnnotation(Attribute.class))
		.ifPresent(attribute -> findMethod(modelclass, "set", field.getName())
		.ifPresent(method -> {
			try {
				method.invoke(model, rs.getObject(attribute.column()));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| SQLException e) {
				
			}
		})));
		
		return modelclass.cast(model);
	}
	
	public <T extends GenericModel> T queryModel(Class<T> modelclass, FilterEntry... parameters) {
		ResultSet rs = queryModelCommon(modelclass, parameters);
		try {
			if (rs.next()) {
				return extractEntry(modelclass, rs);
			}
			return null;
		}catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new DatabaseConnectionException(e);
		}
	}
	
	public <T extends GenericModel> List<T> queryModelList(Class<T> modelclass, FilterEntry... parameters) {
		ResultSet rs = queryModelCommon(modelclass, parameters);
		try {
			List<T> result = new ArrayList<>();
			while (rs.next()) {
				result.add(extractEntry(modelclass, rs));
			}
			return result;
		}catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new DatabaseConnectionException(e);
		}
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}
	
	public <T extends GenericModel> T createModel(Class<T> model, FilterEntry...filterEntries) {
		return Optional.ofNullable(queryModel(model, filterEntries))
				.orElseGet(() -> {
					try {
						Class<?>[] constructorParameterTypes = {};
						Constructor<?> constructor = model.getConstructor(constructorParameterTypes);
						Object[] constructorParameters = {};
						T result = model.cast(constructor.newInstance(constructorParameters));

						Arrays.asList(filterEntries)
						.forEach(filterEntry -> findMethod(model, "set", filterEntry.getPropertyName())
						.ifPresent(method -> {
							try {
								method.invoke(result, filterEntry.getValue());
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								throw new DatabaseConnectionException(e);
							}
						}));

						saveModel(result);

						return result;
					} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
						throw new DatabaseConnectionException(e);
					}
				});
	}
	
	public <T extends GenericModel> List<T> queryModel(Class<T> modelclass1, Class<? extends GenericModel> modelclass2, FilterEntry[][] filterconditions) {
		List<T> result = new ArrayList<>();
		Deployment deployment1 = getDeploymentTag(modelclass1);
		Deployment deployment2 = getDeploymentTag(modelclass2);
		
		if (deployment1 != null && deployment2 != null) {
			List<ParameterEntry> entries1 = new ArrayList<>();
			for (Field field : getFields(modelclass1)) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute != null) {
					ParameterEntry entry = new ParameterEntry(field, attribute.column(), null, null);
					entry.fk = field.getAnnotationsByType(ForeignKey.class);
					entries1.add(entry);
				}
			}
			
			List<ParameterEntry> entries2 = new ArrayList<>();
			for (Field field : getFields(modelclass2)) {
				Attribute attribute = field.getAnnotation(Attribute.class);
				if (attribute != null) {
					ParameterEntry entry = new ParameterEntry(field, attribute.column(),
							findMethod(modelclass2, "set", field.getName()).map(method -> Arrays.asList(method.getParameterTypes()).stream().findFirst().orElse(null)).orElse(null),
							null);
					entry.fk = field.getAnnotationsByType(ForeignKey.class);
					entries2.add(entry);
				}
			}
			
			List<ParameterEntry> filters = new ArrayList<>();
			
			String projection = getFields(modelclass1).stream()
					.filter(field -> findMethod(modelclass1, "set", field.getName()).isPresent())
					.map(field -> Optional.ofNullable(field.getAnnotation(Attribute.class)))
					.filter(Optional<Attribute>::isPresent)
					.map(attribute -> "table1."+attribute.get().column())
					.collect(Collectors.joining(", "));
			
			String query = "SELECT "
					+
					projection
					+
					" FROM " + deployment1.table() + " AS table1 JOIN " + deployment2.table() + " AS table2 ON "
					+
					entries1.stream()
					.map(entry -> Arrays.asList(entry.fk).stream()
							.filter(fk -> fk.table().equals(deployment2.table()))
							.map(fk -> "table1."+entry.columnname + " = table2." + fk.attribute())
							.collect(Collectors.joining(" AND ")))
					.filter(s -> s.length() != 0)
					.collect(Collectors.joining(" AND "))
					+ " WHERE "
					+
					Arrays.asList(
							entries1.stream()
							.filter(entry ->
							Arrays.asList(filterconditions[0]).stream()
							.anyMatch(parameter ->
							parameter.getPropertyName().equals(entry.property.getName())
							&&
							findMethod(modelclass1, "get", entry.property.getName()).isPresent()))
							.map(entry -> {
								ParameterEntry pentry = new ParameterEntry(
										entry.property, 
										entry.columnname, 
										entry.type, 
										Arrays.asList(filterconditions[0]).stream()
										.filter(parameter -> parameter.getPropertyName().equals(entry.property.getName()))
										.findFirst().get().getValue());
								if (pentry.value == null) {
									return "table1."+entry.columnname + " IS NULL";
								} else {
									filters.add(pentry);
									return "table1."+entry.columnname + " = ?";
								}
							})
							.collect(Collectors.joining(" AND "))
							,
							entries2.stream()
							.filter(entry ->
							Arrays.asList(filterconditions[1]).stream()
							.anyMatch(parameter ->
							parameter.getPropertyName().equals(entry.property.getName())
							&&
							findMethod(modelclass2, "get", entry.property.getName()).isPresent()))
							.map(entry -> {
								ParameterEntry pentry = new ParameterEntry(
										entry.property, 
										entry.columnname, 
										entry.type, 
										Arrays.asList(filterconditions[1]).stream()
										.filter(parameter -> parameter.getPropertyName().equals(entry.property.getName()))
										.findFirst().get().getValue());
								if (pentry.value == null) {
									return "table2."+entry.columnname + " IS NULL";
								} else {
									filters.add(pentry);
									return "table2."+entry.columnname + " = ?";
								}
							})
							.collect(Collectors.joining(" AND "))).stream().filter(s -> s.length() != 0)
					.collect(Collectors.joining(" AND "))
					+
					";";
			
			LOGGER.trace(query);
			
			try {
				PreparedStatement pstatement = datasource.getConnection().prepareStatement(query);
				addParameterValues(pstatement, filters, false);
				ResultSet rs = pstatement.executeQuery();
				while (rs.next()) {
					result.add(extractEntry(modelclass1, rs));
				}
			} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
				throw new DatabaseConnectionException(e);
			}
		}
		
		return result;
	}
	
	private Map<String, PreparedStatement> customStatementCache = new HashMap<>();

	protected PreparedStatement customStatementCacheManager(String key, String query) {
		return Optional.ofNullable(customStatementCache.get(key))
				.orElseGet(() -> {
					try {
						PreparedStatement pstatement = getDatasource().getConnection().prepareStatement(query);
						customStatementCache.put(key, pstatement);
						return pstatement;
					} catch (SQLException e) {
						throw new DatabaseConnectionException(e);
					}
				});
	}
}
