package org.eaSTars.adashboard.service.impl;

import java.util.List;

import org.eaSTars.adashboard.service.JavaBodyDeclarationService;
import org.eaSTars.sca.dao.JavaBodyDeclarationDAO;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaFieldModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaMethodParameterModel;

public class DefaultJavaBodyDeclarationService implements JavaBodyDeclarationService {

	public JavaBodyDeclarationDAO javaBodyDeclarationDAO;

	@Override
	public List<JavaFieldModel> getFields(JavaAssemblyModel javaAssembly) {
		return javaBodyDeclarationDAO.getJavaFields(javaAssembly);
	}
	
	@Override
	public List<JavaMethodModel> getMethods(Integer javaAssemblyID) {
		return javaBodyDeclarationDAO.getJavaMethods(javaAssemblyID);
	}
	
	@Override
	public List<JavaMethodModel> getMethods(JavaAssemblyModel javaAssembly) {
		return javaBodyDeclarationDAO.getJavaMethods(javaAssembly);
	}
	
	@Override
	public List<JavaMethodParameterModel> getJavaMethodParameters(JavaMethodModel javaMethod) {
		return javaBodyDeclarationDAO.getJavaMethodParameters(javaMethod);
	}
	
	public JavaBodyDeclarationDAO getJavaBodyDeclarationDAO() {
		return javaBodyDeclarationDAO;
	}

	public void setJavaBodyDeclarationDAO(JavaBodyDeclarationDAO javaBodyDeclarationDAO) {
		this.javaBodyDeclarationDAO = javaBodyDeclarationDAO;
	}
}
