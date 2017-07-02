package org.eaSTars.adashboard.service;

import java.util.List;

import org.eaSTars.sca.dao.TypeArgumentEntry;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.model.JavaObjectTypeModel;
import org.eaSTars.sca.model.JavaTypeModel;

public interface JavaAssemblyService {

	public JavaModuleModel getJavaModul(Integer id);
	
	public List<JavaAssemblyModel> getChildAssemblies(Integer parentId);
	
	public JavaAssemblyModel getJavaAssembly(Integer id);
	
	public JavaAssemblyModel getJavaAssemblyByAggregate(String name);
	
	public JavaObjectTypeModel getPackageType();
	
	public JavaObjectTypeModel getClassType();
	
	public JavaObjectTypeModel getInterfaceType();
	
	public JavaObjectTypeModel getEnumType();
	
	public JavaObjectTypeModel getAnnotationType();
	
	public JavaObjectTypeModel getObjectTypeByID(Integer id);
	
	public TypeArgumentEntry getJavaExtendsArguments(JavaAssemblyModel javaAssembly);
	
	public List<TypeArgumentEntry> getJavaImplementsArguments(JavaAssemblyModel javaAssembly);
	
	public TypeArgumentEntry getJavaArgument(JavaTypeModel javaType);
}
