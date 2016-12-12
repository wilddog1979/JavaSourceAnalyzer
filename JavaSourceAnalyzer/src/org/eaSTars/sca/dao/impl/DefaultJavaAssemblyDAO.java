package org.eaSTars.sca.dao.impl;

import java.util.Optional;

import org.eaSTars.dblayer.dao.FilterEntry;
import org.eaSTars.dblayer.dao.impl.DefaultAbstractDBLayerDAO;
import org.eaSTars.sca.dao.JavaAssemblyDAO;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaExtendsImplementsModel;
import org.eaSTars.sca.model.JavaObjectTypeModel;
import org.eaSTars.sca.model.JavaTypeModel;

public class DefaultJavaAssemblyDAO extends DefaultAbstractDBLayerDAO implements JavaAssemblyDAO {

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
		return queryModel(JavaObjectTypeModel.class, new FilterEntry("name", name));
	}
	
	@Override
	public JavaAssemblyModel getAssembly(Integer pk) {
		return queryModel(JavaAssemblyModel.class, new FilterEntry("PK", pk));
	}
	
	@Override
	public JavaAssemblyModel getAssembly(String name, JavaAssemblyModel parent) {
		return queryModel(JavaAssemblyModel.class,
				new FilterEntry("name", name),
				new FilterEntry("parentAssemblyID", Optional.ofNullable(parent).map(JavaAssemblyModel::getPK).orElse(null)));
	}
	
	@Override
	public JavaAssemblyModel getAssemblyByAggregate(String name) {
		return queryModel(JavaAssemblyModel.class, new FilterEntry("aggregate", name));
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
}
