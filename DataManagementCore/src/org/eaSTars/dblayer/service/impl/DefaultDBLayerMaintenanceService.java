package org.eaSTars.dblayer.service.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eaSTars.dblayer.dao.DatabaseConnectionException;
import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.GenericModel;

public abstract class DefaultDBLayerMaintenanceService {

	private static final Logger LOGGER = LogManager.getLogger(DefaultDBLayerMaintenanceService.class);
	
	private DataSource datasource;
	
	private List<Field> getFields(Class<?> modelclass) {
		List<Field> result = Arrays.asList(
				Optional.ofNullable(modelclass.getDeclaredFields())
				.orElse(new Field[]{})
				).stream()
		.filter(field -> field.isAnnotationPresent(Attribute.class))
		.collect(Collectors.toList());
		
		if (GenericModel.class.isAssignableFrom(modelclass)) {
			List<Field> temp = getFields(modelclass.getSuperclass());
			temp.addAll(result);
			result = temp;
		}
		
		return result;
	}
	
	private String translateJavaType(Attribute attribute, Class<?> type) {
		String dbtype = "";
		
		if (type.isAssignableFrom(String.class)) {
			dbtype = "VARCHAR(255)";
		} else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
			dbtype = "INT";
			if (attribute.primarykey()) {
				dbtype += " IDENTITY(1,1) PRIMARY KEY";
			}
		} else if (type.isAssignableFrom(Boolean.class)) {
			dbtype = "BIT";
		}
		if (!attribute.nullable()) {
			dbtype += " NOT NULL";
		}
		
		return dbtype;
	}
	
	protected void dropModel(Class<? extends GenericModel> modelclass) {
		Deployment deployment = modelclass.getAnnotation(Deployment.class);
		if (deployment != null) {
			try {
				String sqlstatement = "DROP TABLE "+deployment.table()+";";
				
				LOGGER.trace(sqlstatement);
				datasource.getConnection().prepareStatement(sqlstatement).execute();
			} catch (SQLException e) {
				throw new DatabaseConnectionException(e);
			}
		}
	}
	
	protected void createModel(Class<? extends GenericModel> modelclass) {
		Deployment deployment = modelclass.getAnnotation(Deployment.class);
		if (deployment != null) {
			try {
				String sqlstatement = "CREATE TABLE "+deployment.table()+" (\n\t\t"
						+
						getFields(modelclass).stream()
						.map(field ->
						Optional.ofNullable(field.getAnnotation(Attribute.class))
						.map(attr -> attr.column()+" "+translateJavaType(attr, field.getType())))
						.filter(o -> o.isPresent())
						.map(o -> o.get())
						.collect(Collectors.joining(",\n\t\t"))
						+
						"\n\t);";

				LOGGER.trace(sqlstatement);
				datasource.getConnection().prepareStatement(sqlstatement).execute();
			} catch (SQLException e) {
				throw new DatabaseConnectionException(e);
			}

			Arrays.asList(deployment.defaultContent())
			.forEach(defaultContent -> {
				String sqlstatement = "INSERT INTO "+deployment.table()+" (Name) VALUES (?);";
				
				LOGGER.trace(sqlstatement);
				try {
					PreparedStatement pstatement = datasource.getConnection().prepareStatement(sqlstatement);
					pstatement.setString(1, defaultContent);
					pstatement.executeUpdate();
				} catch (SQLException e) {
					LOGGER.warn("Error while inserting into the table", e);
				}
			});
		}
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}
	
}
