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
	
	/**
	 * Returns the Type Argument Entry (Java Assembly and list of Java Type
	 * Models of the type arguments) of the specific Java Assembly. Practically
	 * it returns the extended type of the given assembly.
	 * 
	 * @param javaAssembly the parent assembly
	 * @return Type Argument Entry containing the reference of the extended
	 * Java Assembly and the list of Java Types
	 */
	public TypeArgumentEntry getJavaExtendsArguments(JavaAssemblyModel javaAssembly);
	
	/**
	 * Returns the list of Type Argument Entries (Java Assembly and list of
	 * Java Type Models of the type arguments) of the specific Java Assembly.
	 * Practically it returns the implemented types of the given assembly.
	 * @param javaAssembly the parent assembly
	 * @return List of Type Argument Entries containing the reference of the
	 * implemented Java Assemblies and the list of their Java Types
	 */
	public List<TypeArgumentEntry> getJavaImplementsArguments(JavaAssemblyModel javaAssembly);
	
	public List<JavaAssemblyModel> getImplementingAssemblies(JavaAssemblyModel javaAssembly);
	
	public TypeArgumentEntry getJavaArgument(JavaTypeModel javaType);
}
