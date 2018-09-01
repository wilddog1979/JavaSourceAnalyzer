package org.eaSTars.sca.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eaSTars.dblayer.dao.DatabaseConnectionException;
import org.eaSTars.dblayer.dao.FilterEntry;
import org.eaSTars.dblayer.dao.impl.DefaultAbstractDBLayerDAO;
import org.eaSTars.sca.dao.JavaTypeDAO;
import org.eaSTars.sca.dao.TypeParameterEntry;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaAssemblyTypeParameterModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaMethodTypeParameterModel;
import org.eaSTars.sca.model.JavaTypeArgumentModel;
import org.eaSTars.sca.model.JavaTypeBoundModel;
import org.eaSTars.sca.model.JavaTypeModel;
import org.eaSTars.sca.model.JavaTypeParameterModel;

public class DefaultJavaTypeDAO extends DefaultAbstractDBLayerDAO implements JavaTypeDAO {
	
	private static final int JAVATYPE_CACHE_SIZE = 500;
	
	private EntityCache<String, JavaTypeModel> javaTypeModelCache = new EntityCache<>(JavaTypeModel.class.getSimpleName(), JAVATYPE_CACHE_SIZE);
	
	@Override
	public JavaTypeModel getJavaType(Integer id) {
		return queryModel(JavaTypeModel.class, new FilterEntry("PK", id));
	}
	
	@Override
	public Optional<JavaTypeModel> getJavaType(JavaAssemblyModel javaAssembly, List<JavaTypeModel> arguments) {
		List<JavaTypeModel> javatypes = queryModelList(JavaTypeModel.class,
				new FilterEntry("javaAssemblyID", javaAssembly.getPK()),
				new FilterEntry("argumentCount", arguments.size()));
		
		return javatypes.stream()
				.filter(javatype -> getJavaTypeArguments(javatype).stream()
						.filter(javatypeargument -> javatypeargument.getOrderNumber() >= 0 &&
						javatypeargument.getOrderNumber() < arguments.size() &&
						arguments.get(javatypeargument.getOrderNumber()).getPK().equals(javatypeargument.getJavaTypeID()))
						.count() == arguments.size())
				.findFirst();
	}
	
	@Override
	public JavaTypeModel createJavaType(JavaAssemblyModel javaAssembly, List<JavaTypeModel> arguments) {
		return javaTypeModelCache.getItemFromCache(
				String.format("ja: %d, args: %s%n", javaAssembly.getPK(),
						arguments.stream().map(a -> a.getPK().toString()).collect(Collectors.joining(",", "[" , "]"))), 
				() -> getJavaType(javaAssembly, arguments)
				.orElseGet(() -> {
					JavaTypeModel result = new JavaTypeModel();
					result.setJavaAssemblyID(javaAssembly.getPK());
					result.setArgumentCount(arguments.size());
					saveModel(result);

					for (int i = 0; i < arguments.size(); ++i) {
						JavaTypeArgumentModel arg = new JavaTypeArgumentModel();
						arg.setParentJavaTypeID(result.getPK());
						arg.setJavaTypeID(arguments.get(i).getPK());
						arg.setOrderNumber(i);
						saveModel(arg);
					}
					return result;
				}));
	}
	
	@Override
	public List<JavaTypeArgumentModel> getJavaTypeArguments(JavaTypeModel javaType) {
		return queryModelList(JavaTypeArgumentModel.class,
				new FilterEntry("parentJavaTypeID", javaType.getPK()));
	}
	
	@Override
	public Optional<JavaTypeParameterModel> getJavaTypeParameter(String typeParameterName,
			List<JavaTypeModel> typeBounds) {
		List<JavaTypeParameterModel> javaTypeParameters = queryModelList(JavaTypeParameterModel.class,
				new FilterEntry("name", typeParameterName),
				new FilterEntry("boundCount", typeBounds.size()));

		return javaTypeParameters.stream()
				.filter(typeparam -> getJavaTypeBounds(typeparam).stream()
						.filter(typebound -> typebound.getOrderNumber() >= 0 &&
						typebound.getOrderNumber() < typeBounds.size() &&
						typeBounds.get(typebound.getOrderNumber()).getPK().equals(typebound.getJavaTypeID()))
						.count() == typeBounds.size())
				.findFirst();
	}

	@Override
	public JavaTypeParameterModel createJavaTypeParameter(String typeParameterName, List<JavaTypeModel> typeBounds) {
		return getJavaTypeParameter(typeParameterName, typeBounds)
				.orElseGet(() -> {
					JavaTypeParameterModel result = new JavaTypeParameterModel();
					result.setName(typeParameterName);
					result.setBoundCount(typeBounds.size());
					saveModel(result);
					
					for (int i = 0; i < typeBounds.size(); ++i) {
						JavaTypeBoundModel bound = new JavaTypeBoundModel();
						bound.setJavaTypeParameterID(result.getPK());
						bound.setJavaTypeID(typeBounds.get(i).getPK());
						bound.setOrderNumber(i);
						saveModel(bound);
					}
					return result;
				});
	}
	
	@Override
	public List<JavaTypeBoundModel> getJavaTypeBounds(JavaTypeParameterModel javaTypeParameter) {
		return queryModelList(JavaTypeBoundModel.class,
				new FilterEntry("javaTypeParameterID", javaTypeParameter.getPK()));
	}
	
	@Override
	public Optional<JavaAssemblyTypeParameterModel> getJavaAssemblyTypeParameter(JavaAssemblyModel javaAssembly,
			JavaTypeParameterModel javaTypeParameter) {
		return Optional.ofNullable(queryModel(JavaAssemblyTypeParameterModel.class,
				new FilterEntry("javaAssemblyID", javaAssembly.getPK()),
				new FilterEntry("javaTypeParameterID", javaTypeParameter.getPK())));
	}
	
	@Override
	public JavaAssemblyTypeParameterModel createJavaAssemblyTypeParameter(JavaAssemblyModel javaAssembly,
			JavaTypeParameterModel javaTypeParameter) {
		return getJavaAssemblyTypeParameter(javaAssembly, javaTypeParameter)
				.orElseGet(() -> {
					JavaAssemblyTypeParameterModel result = new JavaAssemblyTypeParameterModel();
					result.setJavaAssemblyID(javaAssembly.getPK());
					result.setJavaTypeParameterID(javaTypeParameter.getPK());
					saveModel(result);
					return result;
				});
	}
	
	@Override
	public Optional<JavaMethodTypeParameterModel> getJavaMethodTypeParameter(JavaMethodModel javaMethod,
			JavaTypeParameterModel javaTypeParameter) {
		return Optional.ofNullable(queryModel(JavaMethodTypeParameterModel.class,
				new FilterEntry("javaMethodID", javaMethod.getPK()),
				new FilterEntry("javaTypeParameterID", javaTypeParameter.getPK())));
	}
	
	@Override
	public JavaMethodTypeParameterModel createJavaMethodTypeParameter(JavaMethodModel javaMethod,
			JavaTypeParameterModel javaTypeParameter) {
		return getJavaMethodTypeParameter(javaMethod, javaTypeParameter)
				.orElseGet(() -> {
					JavaMethodTypeParameterModel result = new JavaMethodTypeParameterModel();
					result.setJavaMethodID(javaMethod.getPK());
					result.setJavaTypeParameterID(javaTypeParameter.getPK());
					saveModel(result);
					return result;
				});
	}
	
	@Override
	public JavaTypeParameterModel getJavaTypeParameterByAssembly(JavaAssemblyModel javaAssembly, String name) {
		try {
			PreparedStatement pstatement = customStatementCacheManager("DefaultJavaTypeDAO.getJavaTypeParameterByAssembly",
					"SELECT jtp.PK, jtp.Name, jtp.BoundCount FROM JavaTypeParameter AS jtp JOIN JavaAssemblyTypeParameter AS jatp ON jtp.PK = jatp.JavaTypeParameterID WHERE jatp.JavaAssemblyID = ? AND jtp.Name = ?");
			pstatement.setInt(1, javaAssembly.getPK());
			pstatement.setString(2, name);
			ResultSet rs = pstatement.executeQuery();
			if (rs.next()) {
				return extractEntry(JavaTypeParameterModel.class, rs);
			}
			return null;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new DatabaseConnectionException(e);
		}
	}
	
	@Override
	public List<TypeParameterEntry> getJavaAssemblyTypeParameters(JavaAssemblyModel javaAssembly) {
		try {
			List<TypeParameterEntry> result = new ArrayList<>();
			PreparedStatement pstatement = customStatementCacheManager("DefaultJavaTypeDAO.getJavaAssemblyTypeParameters",
					"SELECT jtp.* FROM JavaTypeParameter AS jtp JOIN JavaAssemblyTypeParameter AS jatp ON jtp.PK = jatp.JavaTypeParameterID WHERE jatp.JavaAssemblyID = ?;");
			pstatement.setInt(1, javaAssembly.getPK());
			ResultSet rs = pstatement.executeQuery();
			while (rs.next()) {
				TypeParameterEntry tpe = new TypeParameterEntry();
				tpe.setTypeParameter(extractEntry(JavaTypeParameterModel.class, rs));
				tpe.getBounds().addAll(getTypeBoundsOfTypeParameter(tpe.getTypeParameter()));
				result.add(tpe);
			}
			return result;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new DatabaseConnectionException(e);
		}
	}
	
	@Override
	public List<TypeParameterEntry> getJavaMethodTypeParameters(JavaMethodModel javaMethod) {
		try {
			List<TypeParameterEntry> result = new ArrayList<>();
			PreparedStatement pstatement = customStatementCacheManager("DefaultJavaTypeDAO.getJavaMethodTypeParameters",
					"SELECT jtp.* FROM JavaTypeParameter AS jtp JOIN JavaMethodTypeParameter AS jmtp ON jtp.PK = jmtp.JavaTypeParameterID WHERE jmtp.JavaMethodID = ?;");
			pstatement.setInt(1, javaMethod.getPK());
			ResultSet rs = pstatement.executeQuery();
			while (rs.next()) {
				TypeParameterEntry tpe = new TypeParameterEntry();
				tpe.setTypeParameter(extractEntry(JavaTypeParameterModel.class, rs));
				tpe.getBounds().addAll(getTypeBoundsOfTypeParameter(tpe.getTypeParameter()));
				result.add(tpe);
			}
			return result;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new DatabaseConnectionException(e);
		}
	}
	
	@Override
	public List<JavaAssemblyModel> getTypeBoundsOfTypeParameter(JavaTypeParameterModel javaTypeParameter) {
		try {
			List<JavaAssemblyModel> result = new ArrayList<>();
			PreparedStatement pstatement = customStatementCacheManager("DefaultJavaTypeDAO.getTypeBoundsOfTypeParameter",
					"SELECT ja.* FROM JavaAssembly AS ja JOIN JavaType AS jt ON ja.PK = jt.JavaAssemblyID JOIN JavaTypeBound AS jtb ON jt.PK = jtb.JavaTypeID WHERE jtb.JavaTypeParameterID = ? ORDER BY jtb.OrderNumber ASC;");
			pstatement.setInt(1, javaTypeParameter.getPK());
			ResultSet rs = pstatement.executeQuery();
			while (rs.next()) {
				result.add(extractEntry(JavaAssemblyModel.class, rs));
			}
			return result;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new DatabaseConnectionException(e);
		}
	}
	
	@Override
	public List<JavaTypeModel> getTypeArguments(JavaTypeModel javaType) {
		try {
			List<JavaTypeModel> result = new ArrayList<>();
			PreparedStatement pstatement = customStatementCacheManager("DefaultJavaTypeDAO.getTypeArguments",
					"SELECT jt.* FROM JavaType AS jt JOIN JavaTypeArgument AS jta ON jt.PK = jta.JavaTypeID WHERE jta.ParentJavaTypeID = ? ORDER BY jta.OrderNumber ASC;");
			pstatement.setInt(1, javaType.getPK());
			ResultSet rs = pstatement.executeQuery();
			while (rs.next()) {
				result.add(extractEntry(JavaTypeModel.class, rs));
			}
			return result;
		} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			throw new DatabaseConnectionException(e);
		}
	}
}
