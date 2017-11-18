package org.eaSTars.sca.dao;

import java.util.List;
import java.util.Optional;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaAssemblyTypeParameterModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaMethodTypeParameterModel;
import org.eaSTars.sca.model.JavaTypeArgumentModel;
import org.eaSTars.sca.model.JavaTypeBoundModel;
import org.eaSTars.sca.model.JavaTypeModel;
import org.eaSTars.sca.model.JavaTypeParameterModel;

public interface JavaTypeDAO {

	public JavaTypeModel getJavaType(Integer id);
	
	public Optional<JavaTypeModel> getJavaType(JavaAssemblyModel javaAssembly, List<JavaTypeModel> arguments);
	
	public JavaTypeModel createJavaType(JavaAssemblyModel javaAssembly, List<JavaTypeModel> arguments);
	
	public List<JavaTypeArgumentModel> getJavaTypeArguments(JavaTypeModel javaType);
	
	public Optional<JavaTypeParameterModel> getJavaTypeParameter(String typeParameterName, List<JavaTypeModel> typeBounds);
	
	public JavaTypeParameterModel createJavaTypeParameter(String typeParameterName, List<JavaTypeModel> typeBounds);
	
	public List<JavaTypeBoundModel> getJavaTypeBounds(JavaTypeParameterModel javaTypeParameter);
	
	public Optional<JavaAssemblyTypeParameterModel> getJavaAssemblyTypeParameter(JavaAssemblyModel javaAssembly, JavaTypeParameterModel javaTypeParameter);
	
	public List<TypeParameterEntry> getJavaAssemblyTypeParameters(JavaAssemblyModel javaAssembly);
	
	public List<JavaAssemblyModel> getTypeBoundsOfTypeParameter(JavaTypeParameterModel javaTypeParameter);
	
	public JavaAssemblyTypeParameterModel createJavaAssemblyTypeParameter(JavaAssemblyModel javaAssembly, JavaTypeParameterModel javaTypeParameter);
	
	public Optional<JavaMethodTypeParameterModel> getJavaMethodTypeParameter(JavaMethodModel javaMethod, JavaTypeParameterModel javaTypeParameter);
	
	public JavaMethodTypeParameterModel createJavaMethodTypeParameter(JavaMethodModel javaMethod, JavaTypeParameterModel javaTypeParameter);
	
	public JavaTypeParameterModel getJavaTypeParameterByAssembly(JavaAssemblyModel javaAssembly, String name);
	
	public List<JavaTypeModel> getTypeArguments(JavaTypeModel javaType);
	
	public List<TypeParameterEntry> getJavaMethodTypeParameters(JavaMethodModel javaMethod);
}
