package org.eaSTars.adashboard.service.impl;

import java.util.List;

import org.eaSTars.adashboard.service.JavaTypeService;
import org.eaSTars.sca.dao.JavaTypeDAO;
import org.eaSTars.sca.dao.TypeParameterEntry;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaTypeModel;

public class DefaultJavaTypeService implements JavaTypeService {

	private JavaTypeDAO javaTypeDAO;

	@Override
	public JavaTypeModel getJavaType(Integer id) {
		return javaTypeDAO.getJavaType(id);
	}
	
	@Override
	public List<JavaTypeModel> getTypeArguments(JavaTypeModel javaType) {
		return javaTypeDAO.getTypeArguments(javaType);
	}
	
	@Override
	public List<TypeParameterEntry> getJavaAssemblyTypeParameters(JavaAssemblyModel javaAssembly) {
		return javaTypeDAO.getJavaAssemblyTypeParameters(javaAssembly);
	}
	
	@Override
	public List<TypeParameterEntry> getJavaMethodTypeParameters(JavaMethodModel javaMethod) {
		return javaTypeDAO.getJavaMethodTypeParameters(javaMethod);
	}
	
	public JavaTypeDAO getJavaTypeDAO() {
		return javaTypeDAO;
	}

	public void setJavaTypeDAO(JavaTypeDAO javaTypeDAO) {
		this.javaTypeDAO = javaTypeDAO;
	}
}
