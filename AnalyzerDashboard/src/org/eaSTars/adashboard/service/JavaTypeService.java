package org.eaSTars.adashboard.service;

import java.util.List;

import org.eaSTars.sca.dao.TypeParameterEntry;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaTypeModel;

public interface JavaTypeService {

	public JavaTypeModel getJavaType(Integer id);
	
	public List<JavaTypeModel> getTypeArguments(JavaTypeModel javaType);
	
	public List<TypeParameterEntry> getJavaAssemblyTypeParameters(JavaAssemblyModel javaAssembly);
	
	public List<TypeParameterEntry> getJavaMethodTypeParameters(JavaMethodModel javaMethod);
}
