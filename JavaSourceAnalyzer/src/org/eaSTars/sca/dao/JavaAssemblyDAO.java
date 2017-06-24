package org.eaSTars.sca.dao;

import java.util.List;

import org.eaSTars.dblayer.dao.AbstractDBLayerDAO;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaExtendsImplementsModel;
import org.eaSTars.sca.model.JavaObjectTypeModel;
import org.eaSTars.sca.model.JavaTypeModel;

public interface JavaAssemblyDAO extends AbstractDBLayerDAO {

	public JavaObjectTypeModel getPackageObjectType();
	
	public JavaObjectTypeModel getClassObjectType();
	
	public JavaObjectTypeModel getInterfaceObjectType();
	
	public JavaObjectTypeModel getEnumObjectType();
	
	public JavaObjectTypeModel getAnnotationType();
	
	public JavaAssemblyModel getAssembly(Integer pk);
	
	public JavaAssemblyModel getAssembly(String name, JavaAssemblyModel parent);
	
	public JavaAssemblyModel getAssemblyByAggregate(String name);
	
	public JavaExtendsImplementsModel getExtends(JavaAssemblyModel parentAssembly, JavaTypeModel javaType);
	
	public JavaExtendsImplementsModel createExtends(JavaAssemblyModel parentAssembly, JavaTypeModel javaType);
	
	public JavaExtendsImplementsModel getImplements(JavaAssemblyModel parentAssembly, JavaTypeModel javaType);
	
	public JavaExtendsImplementsModel createImplements(JavaAssemblyModel parentAssembly, JavaTypeModel javaType);
	
	public List<JavaAssemblyModel> getAssembliesByParent(Integer parentid);
	
	public JavaTypeModel getJavaAssemblyExtends(JavaAssemblyModel javaAssembly);
	
	public List<JavaTypeModel> getJavaAssemblyImplements(JavaAssemblyModel javaAssembly);
	
	public void saveModel(JavaAssemblyModel model);
}
