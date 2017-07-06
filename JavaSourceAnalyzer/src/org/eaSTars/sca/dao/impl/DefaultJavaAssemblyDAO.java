package org.eaSTars.sca.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eaSTars.dblayer.dao.DatabaseConnectionException;
import org.eaSTars.dblayer.dao.FilterEntry;
import org.eaSTars.dblayer.dao.impl.DefaultAbstractDBLayerDAO;
import org.eaSTars.sca.dao.JavaAssemblyDAO;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaExtendsImplementsModel;
import org.eaSTars.sca.model.JavaObjectTypeModel;
import org.eaSTars.sca.model.JavaTypeModel;

public class DefaultJavaAssemblyDAO extends DefaultAbstractDBLayerDAO implements JavaAssemblyDAO {
	
	private final static int JAVAASSEMBLY_CACHE_SIZE = 500;
	
	private final static int IMPLEMENTS = 0;
	
	private final static int EXTENDS = 1;
	
	private Map<String, JavaObjectTypeModel> objectTypeCache = new HashMap<String, JavaObjectTypeModel>();
	
	private EntityCache<String, JavaAssemblyModel> javaAssemblyEntityCache = new EntityCache<>(JavaAssemblyModel.class.getSimpleName(), JAVAASSEMBLY_CACHE_SIZE);
	
	@Override
	public JavaObjectTypeModel getPackageObjectType() {
		return getObjectType("Package");
	}

	@Override
	public JavaObjectTypeModel getClassObjectType() {
		return getObjectType("Class");
	}

	@Override
	public JavaObjectTypeModel getInterfaceObjectType() {
		return getObjectType("Interface");
	}

	@Override
	public JavaObjectTypeModel getEnumObjectType() {
		return getObjectType("Enum");
	}
	
	@Override
	public JavaObjectTypeModel getAnnotationType() {
		return getObjectType("Annotation");
	}

	private JavaObjectTypeModel getObjectType(String name) {
		return Optional.ofNullable(objectTypeCache.get(name))
				.orElseGet(() -> {
					JavaObjectTypeModel result = queryModel(JavaObjectTypeModel.class, new FilterEntry("name", name));
					objectTypeCache.put(name, result);
					return result;
				});
	}

	@Override
	public JavaAssemblyModel getAssembly(Integer pk) {
		return queryModel(JavaAssemblyModel.class, new FilterEntry("PK", pk));
	}
	
	@Override
	public JavaAssemblyModel getAssembly(String name, JavaAssemblyModel parent) {
		return queryModel(JavaAssemblyModel.class,
				new FilterEntry("name", name),
				new FilterEntry("parentAssemblyID", Optional.ofNullable(parent)
						.map(JavaAssemblyModel::getPK).orElse(null)));
	}
	
	@Override
	public JavaAssemblyModel getAssemblyByAggregate(String name) {
		return javaAssemblyEntityCache.getItemFromCache(name,
				() -> queryModel(JavaAssemblyModel.class, new FilterEntry("aggregate", name)));
	}

	@Override
	public JavaExtendsImplementsModel getExtends(JavaAssemblyModel parentAssembly, JavaTypeModel javaType) {
		return queryModel(JavaExtendsImplementsModel.class,
				new FilterEntry("parentJavaAssemblyID", parentAssembly.getPK()),
				new FilterEntry("javaTypeID", javaType.getPK()),
				new FilterEntry("isExtends", true));
	}

	@Override
	public JavaExtendsImplementsModel createExtends(JavaAssemblyModel parentAssembly, JavaTypeModel javaType) {
		return createModel(JavaExtendsImplementsModel.class,
				new FilterEntry("parentJavaAssemblyID", parentAssembly.getPK()),
				new FilterEntry("javaTypeID", javaType.getPK()),
				new FilterEntry("isExtends", true));
	}
	
	@Override
	public JavaExtendsImplementsModel getImplements(JavaAssemblyModel parentAssembly, JavaTypeModel javaType) {
		return queryModel(JavaExtendsImplementsModel.class,
				new FilterEntry("parentJavaAssemblyID", parentAssembly.getPK()),
				new FilterEntry("javaTypeID", javaType.getPK()),
				new FilterEntry("isExtends", false));
	}
	
	@Override
	public JavaExtendsImplementsModel createImplements(JavaAssemblyModel parentAssembly, JavaTypeModel javaType) {
		return createModel(JavaExtendsImplementsModel.class,
				new FilterEntry("parentJavaAssemblyID", parentAssembly.getPK()),
				new FilterEntry("javaTypeID", javaType.getPK()),
				new FilterEntry("isExtends", false));
	}
	
	@Override
	public List<JavaAssemblyModel> getAssembliesByParent(Integer parentid) {
		return queryModelList(JavaAssemblyModel.class,
				new FilterEntry("parentAssemblyID", parentid));
	}
	
	/**
	 * Runs the query to find what type is being extended or implemented
	 * by the specified JavaAssembly. 
	 * 
	 * @param javaAssembly parent assembly
	 * @param isExtends 1 is for extends, 2 is for implements
	 * @return
	 * @throws SQLException 
	 */
	private ResultSet getJavaAssemblyExtendsImplements(JavaAssemblyModel javaAssembly, int isExtends) throws SQLException {
		PreparedStatement pstatement = customStatementCacheManager("getJavaAssemblyExtendsImplements",
				"SELECT jt.* FROM JavaType AS jt JOIN JavaExtendsImplements AS jei ON jt.PK = jei.JavaTypeID AND jei.isExtends = ? JOIN JavaAssembly AS ja ON jei.ParentJavaAssemblyID = ja.PK WHERE ja.PK = ?;");
		pstatement.setInt(1, isExtends);
		pstatement.setInt(2, javaAssembly.getPK());
		return pstatement.executeQuery();
	}
	
	private ResultSet getJavaAssemblyExtendingImplementing(JavaAssemblyModel javaAssembly, int isExtends) throws SQLException {
		PreparedStatement pstatement = customStatementCacheManager("getJavaAssemblyExtendingImplementing",
				"SELECT ja.* FROM JavaAssembly AS ja JOIN JavaExtendsImplements AS jei ON ja.PK = jei.ParentJavaAssemblyID AND jei.isExtends = ? JOIN JavaType AS jt ON jei.JavaTypeID = jt.PK WHERE jt.JavaAssemblyID = ?;");
		pstatement.setInt(1, isExtends);
		pstatement.setInt(2, javaAssembly.getPK());
		return pstatement.executeQuery();
	}
	
	@Override
	public JavaTypeModel getJavaAssemblyExtends(JavaAssemblyModel javaAssembly) {
		try {
			ResultSet rs = getJavaAssemblyExtendsImplements(javaAssembly, EXTENDS);
			if (rs.next()) {
				return extractEntry(JavaTypeModel.class, rs);
			}
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			throw new DatabaseConnectionException(e);
		}
		return null;
	}
	
	@Override
	public List<JavaTypeModel> getJavaAssemblyImplements(JavaAssemblyModel javaAssembly) {
		List<JavaTypeModel> result = new ArrayList<JavaTypeModel>();
		try {
			ResultSet rs = getJavaAssemblyExtendsImplements(javaAssembly, IMPLEMENTS);
			while (rs.next()) {
				result.add(extractEntry(JavaTypeModel.class, rs));
			}
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			throw new DatabaseConnectionException(e);
		}
		return result;
	}
	
	@Override
	public List<JavaAssemblyModel> getJavaAssemblyImplemented(JavaAssemblyModel javaAssembly) {
		List<JavaAssemblyModel> result = new ArrayList<JavaAssemblyModel>();
		try {
			ResultSet rs = getJavaAssemblyExtendingImplementing(javaAssembly, IMPLEMENTS);
			while (rs.next()) {
				result.add(extractEntry(JavaAssemblyModel.class, rs));
			}
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			throw new DatabaseConnectionException(e);
		}
		return result;
	}
	
	private ResultSet getJavaAssemblyExtendedImplemented(JavaTypeModel javaType, int isExtends) throws SQLException {
		PreparedStatement pstatement = customStatementCacheManager("getJavaAssemblyExtendingImplementing",
				"SELECT ja.* FROM JavaAssembly AS ja JOIN JavaExtendsImplements AS jei ON ja.PK = jei.ParentJavaAssemblyID AND jei.isExtends = ? WHERE jei.JavaTypeID = ?;");
		pstatement.setInt(1, isExtends);
		pstatement.setInt(2, javaType.getPK());
		return pstatement.executeQuery();
	}
	
	@Override
	public List<JavaAssemblyModel> getJavaAssemblyImplemented(JavaTypeModel javaType) {
		List<JavaAssemblyModel> result = new ArrayList<JavaAssemblyModel>();
		try {
			ResultSet rs = getJavaAssemblyExtendedImplemented(javaType, IMPLEMENTS);
			while (rs.next()) {
				result.add(extractEntry(JavaAssemblyModel.class, rs));
			}
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			throw new DatabaseConnectionException(e);
		}
		return result;
	}
	
	@Override
	public List<JavaTypeModel> getJavaTypes(JavaAssemblyModel javaAssembly, int argumentcount) {
		return queryModelList(JavaTypeModel.class,
				new FilterEntry("javaAssemblyID", javaAssembly.getPK()),
				new FilterEntry("argumentCount", argumentcount));
	}
	
	@Override
	public List<JavaTypeModel> getJavaTypeArguments(JavaTypeModel javaType) {
		try {
			PreparedStatement pstatement = customStatementCacheManager("getJavaTypeArguments",
					"SELECT jt.* FROM JavaType AS jt JOIN JavaTypeArgument AS jta ON jt.PK = jta.JavaTypeID WHERE jta.ParentJavaTypeID = ? ORDER BY jta.OrderNumber;");
			pstatement.setInt(1, javaType.getPK());
			
			List<JavaTypeModel> result = new ArrayList<JavaTypeModel>();
			ResultSet rs = pstatement.executeQuery();
			while (rs.next()) {
				result.add(extractEntry(JavaTypeModel.class, rs));
			}
			return result;
		} catch (SQLException | InstantiationException | IllegalAccessException e) {
			throw new DatabaseConnectionException(e);
		}
	}
	
	@Override
	public void saveModel(JavaAssemblyModel model) {
		super.saveModel(model);
		javaAssemblyEntityCache.putInModelCache(model.getAggregate(), model);
	}
	
}
