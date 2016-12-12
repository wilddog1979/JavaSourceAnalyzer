package org.eaSTars.binprocessor.service.impl;

import java.util.Optional;

import org.eaSTars.sca.dao.JavaAssemblyDAO;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.model.JavaObjectTypeModel;

public abstract class AbstractBinaryParser {

	private JavaAssemblyDAO javaAssemblyDAO;
	
	private JavaObjectTypeModel javaPackageType;
	
	private JavaObjectTypeModel javaClassType;
	
	private JavaObjectTypeModel javaInterfaceType;
	
	private JavaObjectTypeModel javaEnumType;
	
	private JavaObjectTypeModel javaAnnotationType;
	
	protected JavaAssemblyModel createJavaPackageStructure(String packagename, boolean confirmed) {
		int index = packagename.lastIndexOf('.');
		JavaAssemblyModel result = javaAssemblyDAO.getAssemblyByAggregate(packagename);
		if (result == null) {
			result = new JavaAssemblyModel();
			result.setName(packagename.substring(index + 1));
			if (index != -1) {
				result.setParentAssemblyID(Optional.ofNullable(createJavaPackageStructure(packagename.substring(0, index), true))
						.map(p -> p.getPK())
						.orElse(null));
			}
			result.setConfirmed(confirmed);
			result.setJavaObjectTypeID(getJavaPackageType().getPK());
			result.setAggregate(packagename);
			javaAssemblyDAO.saveModel(result);
		}
		return result;
	}
	
	protected JavaAssemblyModel createOtherStructure(JavaAssemblyModel parent, String name, boolean confirmed, JavaModuleModel module, JavaObjectTypeModel objecttype) {
		JavaAssemblyModel result = javaAssemblyDAO.getAssembly(name, parent);
		if (result == null) {
			result = new JavaAssemblyModel();
			result.setName(name);
			result.setParentAssemblyID(Optional.ofNullable(parent).map(p -> p.getPK()).orElse(null));
			result.setConfirmed(confirmed);
			result.setJavaObjectTypeID(objecttype.getPK());
			result.setAggregate(Optional.ofNullable(parent).map(p -> p.getAggregate()+".").orElse("")+name);
			result.setJavaModuleID(module.getPK());
			javaAssemblyDAO.saveModel(result);
		} else if (confirmed && !result.getConfirmed()) {
			result.setJavaObjectTypeID(objecttype.getPK());
			result.setConfirmed(confirmed);
			javaAssemblyDAO.saveModel(result);
		}
		return result;
	}

	public JavaAssemblyDAO getJavaAssemblyDAO() {
		return javaAssemblyDAO;
	}

	public void setJavaAssemblyDAO(JavaAssemblyDAO javaAssemblyDAO) {
		this.javaAssemblyDAO = javaAssemblyDAO;
	}
	
	public JavaObjectTypeModel getJavaPackageType() {
		if (javaPackageType == null) {
			javaPackageType = javaAssemblyDAO.getPackageObjectType();
		}
		return javaPackageType;
	}

	public JavaObjectTypeModel getJavaClassType() {
		if (javaClassType == null) {
			javaClassType = javaAssemblyDAO.getClassObjectType();
		}
		return javaClassType;
	}

	public JavaObjectTypeModel getJavaInterfaceType() {
		if (javaInterfaceType == null) {
			javaInterfaceType = javaAssemblyDAO.getInterfaceObjectType();
		}
		return javaInterfaceType;
	}

	public JavaObjectTypeModel getJavaEnumType() {
		if (javaEnumType == null) {
			javaEnumType = javaAssemblyDAO.getEnumObjectType();
		}
		return javaEnumType;
	}

	public JavaObjectTypeModel getJavaAnnotationType() {
		if (javaAnnotationType == null) {
			javaAnnotationType = javaAssemblyDAO.getAnnotationType();
		}
		return javaAnnotationType;
	}
}
