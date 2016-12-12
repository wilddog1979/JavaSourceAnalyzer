package org.eaSTars.sca.dao;

import java.util.List;
import java.util.Optional;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaFieldModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaMethodParameterModel;
import org.eaSTars.sca.model.JavaThrowsModel;
import org.eaSTars.sca.model.JavaTypeModel;

public interface JavaBodyDeclarationDAO {

	public JavaFieldModel getJavaField(JavaAssemblyModel parentAssembly, JavaTypeModel javatpye, String name);
	
	public JavaFieldModel createJavaField(JavaAssemblyModel parentAssembly, int modifiers, JavaTypeModel javatpye, String name);
	
	public JavaMethodModel getJavaMethod(JavaAssemblyModel parentAssembly, JavaTypeModel javatpye, String name, int parametercount);
	
	public List<JavaMethodModel> getJavaMethods(JavaAssemblyModel parentAssembly, String name, int parametercount);
	
	public List<JavaMethodParameterModel> getJavaMethodParameters(JavaMethodModel javaMethod);
	
	public Optional<JavaMethodParameterModel> getJavaMethodParameter(JavaMethodModel javaMethod, int orderNumber);
	
	public JavaMethodParameterModel createJavaMethodParameter(JavaMethodModel javaMethod, String name, int orderNumber, JavaTypeModel javaType);
	
	public JavaMethodModel createMethod(JavaAssemblyModel parentAssembly, int modifiers, JavaTypeModel javatpye, String name, int parametercount);
	
	public Optional<JavaThrowsModel> getJavathrows(JavaMethodModel javamethod, JavaTypeModel javatype);
	
	public JavaThrowsModel createJavathrows(JavaMethodModel javamethod, JavaTypeModel javatype);
}
