package org.eaSTars.adashboard.service;

import java.util.List;

import org.eaSTars.adashboard.service.dto.TypeDescriptor;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaFieldModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaMethodParameterModel;

public interface JavaBodyDeclarationService {

	public List<JavaFieldModel> getFields(JavaAssemblyModel javaAssembly);
	
	public JavaMethodModel getMethod(Integer id);
	
	public List<JavaMethodModel> getMethods(Integer javaAssemblyID);
	
	public List<JavaMethodModel> getMethods(JavaAssemblyModel javaAssembly);
	
	public List<JavaMethodModel> getMethods(JavaAssemblyModel javaAssembly, String name, List<TypeDescriptor> parametertypes);
	
	public List<JavaMethodParameterModel> getJavaMethodParameters(JavaMethodModel javaMethod);
}
