package org.eaSTars.sca.service.impl;

import java.util.Optional;

import org.eaSTars.sca.dao.JavaAssemblyDAO;
import org.eaSTars.sca.dao.JavaBodyDeclarationDAO;
import org.eaSTars.sca.dao.JavaTypeDAO;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.model.JavaObjectTypeModel;
import org.eaSTars.sca.service.JavaDeclarationParser;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;

public abstract class AbstractJavaParser {

	private JavaAssemblyDAO javaAssemblyDAO;
	
	private JavaObjectTypeModel javaPackageType;
	
	private JavaObjectTypeModel javaClassType;
	
	private JavaObjectTypeModel javaInterfaceType;
	
	private JavaObjectTypeModel javaEnumType;
	
	private JavaObjectTypeModel javaAnnotationType;
	
	private JavaTypeDAO javaTypeDAO;
	
	private JavaBodyDeclarationDAO javaBodyDeclarationDAO;
	
	private JavaDeclarationParser javaDeclarationParser;
	
	public JavaAssemblyModel createJavaPackageStructure(NameExpr nameExpr, boolean confirmed) {
		JavaAssemblyModel result = javaAssemblyDAO.getAssemblyByAggregate(nameExpr.toStringWithoutComments());
		if (result == null) {
			JavaAssemblyModel parent = null;
			if (nameExpr instanceof QualifiedNameExpr) {
				parent = createJavaPackageStructure(((QualifiedNameExpr)nameExpr).getQualifier(), confirmed);
			}
			result = new JavaAssemblyModel();
			result.setName(nameExpr.getName());
			result.setParentAssemblyID(Optional.ofNullable(parent).map(p -> p.getPK()).orElse(null));
			result.setConfirmed(confirmed);
			result.setJavaObjectTypeID(getJavaPackageType().getPK());
			result.setAggregate(nameExpr.toStringWithoutComments());
			javaAssemblyDAO.saveModel(result);
		} else if (confirmed) {
			JavaAssemblyModel toconfirm = result;
			while (toconfirm != null && !toconfirm.getConfirmed()) {
				toconfirm.setJavaObjectTypeID(getJavaPackageType().getPK());
				toconfirm.setConfirmed(confirmed);
				javaAssemblyDAO.saveModel(toconfirm);
				toconfirm = Optional.ofNullable(toconfirm.getParentAssemblyID())
						.map(parentpk -> javaAssemblyDAO.getAssembly(parentpk))
						.orElse(null);
			}
		}
		return result;
	}

	public JavaAssemblyModel createOtherStructure(NameExpr nameExpr, boolean confirmed, JavaModuleModel module, JavaObjectTypeModel objecttype, Integer modifiers) {
		JavaAssemblyModel result = javaAssemblyDAO.getAssemblyByAggregate(nameExpr.toStringWithoutComments());
		if (result == null) {
			JavaAssemblyModel parent = null;
			if (nameExpr instanceof QualifiedNameExpr) {
				parent = createJavaPackageStructure(((QualifiedNameExpr)nameExpr).getQualifier(), false);
			}
			result = new JavaAssemblyModel();
			result.setModifiers(modifiers);
			result.setName(nameExpr.getName());
			result.setParentAssemblyID(Optional.ofNullable(parent).map(p -> p.getPK()).orElse(null));
			result.setConfirmed(confirmed);
			result.setJavaObjectTypeID(objecttype.getPK());
			result.setAggregate(Optional.ofNullable(parent).map(p -> p.getAggregate()+".").orElse("")+nameExpr.getName());
			result.setJavaModuleID(module.getPK());
			javaAssemblyDAO.saveModel(result);
		} else if (confirmed && !result.getConfirmed()) {
			result.setJavaObjectTypeID(objecttype.getPK());
			result.setModifiers(modifiers);
			result.setConfirmed(confirmed);
			result.setJavaModuleID(Optional.of(module).map(m -> m.getPK()).orElse(result.getJavaModuleID()));
			javaAssemblyDAO.saveModel(result);
		}
		return result;
	}
	
	public JavaAssemblyModel createOtherStructure(JavaAssemblyModel parent, String name, boolean confirmed, JavaModuleModel module, JavaObjectTypeModel objecttype, Integer modifiers) {
		JavaAssemblyModel result = javaAssemblyDAO.getAssembly(name, parent);
		if (result == null) {
			result = new JavaAssemblyModel();
			result.setModifiers(modifiers);
			result.setName(name);
			result.setParentAssemblyID(Optional.ofNullable(parent).map(p -> p.getPK()).orElse(null));
			result.setConfirmed(confirmed);
			result.setJavaObjectTypeID(objecttype.getPK());
			result.setAggregate(Optional.ofNullable(parent).map(p -> p.getAggregate()+".").orElse("")+name);
			result.setJavaModuleID(module.getPK());
			javaAssemblyDAO.saveModel(result);
		} else if (confirmed && !result.getConfirmed()) {
			result.setJavaObjectTypeID(objecttype.getPK());
			result.setModifiers(modifiers);
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

	public JavaTypeDAO getJavaTypeDAO() {
		return javaTypeDAO;
	}

	public void setJavaTypeDAO(JavaTypeDAO javaTypeDAO) {
		this.javaTypeDAO = javaTypeDAO;
	}

	public JavaBodyDeclarationDAO getJavaBodyDeclarationDAO() {
		return javaBodyDeclarationDAO;
	}

	public void setJavaBodyDeclarationDAO(JavaBodyDeclarationDAO javaBodyDeclarationDAO) {
		this.javaBodyDeclarationDAO = javaBodyDeclarationDAO;
	}

	public JavaDeclarationParser getJavaDeclarationParser() {
		return javaDeclarationParser;
	}

	public void setJavaDeclarationParser(JavaDeclarationParser javaDeclarationParser) {
		this.javaDeclarationParser = javaDeclarationParser;
	}
}
