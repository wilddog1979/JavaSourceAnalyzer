package org.eaSTars.adashboard.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eaSTars.adashboard.service.JavaAssemblyService;
import org.eaSTars.sca.dao.JavaAssemblyDAO;
import org.eaSTars.sca.dao.JavaTypeDAO;
import org.eaSTars.sca.dao.TypeArgumentEntry;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.model.JavaObjectTypeModel;
import org.eaSTars.sca.model.JavaTypeModel;

public class DefaultJavaAssemblyService implements JavaAssemblyService {

	private JavaAssemblyDAO javaAssemblyDAO;
	
	private JavaTypeDAO javaTypeDAO;
	
	private JavaObjectTypeModel packagetype;
	
	private JavaObjectTypeModel classtype;
	
	private JavaObjectTypeModel interfacetype;
	
	private JavaObjectTypeModel enumtype;
	
	private JavaObjectTypeModel annotationtype;
	
	@Override
	public JavaModuleModel getJavaModul(Integer id) {
		return javaAssemblyDAO.getModelByPK(JavaModuleModel.class, id);
	}
	
	@Override
	public List<JavaAssemblyModel> getChildAssemblies(Integer parentId) {
		return javaAssemblyDAO.getAssembliesByParent(parentId);
	}
	
	@Override
	public JavaAssemblyModel getAssembly(Integer parentId, String name) {
		return javaAssemblyDAO.getAssembly(name, javaAssemblyDAO.getModelByPK(JavaAssemblyModel.class, parentId));
	}
	
	@Override
	public JavaAssemblyModel getJavaAssembly(Integer id) {
		return javaAssemblyDAO.getAssembly(id);
	}
	
	@Override
	public JavaAssemblyModel getJavaAssemblyByAggregate(String name) {
		return javaAssemblyDAO.getAssemblyByAggregate(name);
	}

	@Override
	public JavaObjectTypeModel getPackageType() {
		if (packagetype == null) {
			packagetype = javaAssemblyDAO.getPackageObjectType();
		}
		return packagetype;
	}
	
	@Override
	public JavaObjectTypeModel getClassType() {
		if (classtype == null) {
			classtype = javaAssemblyDAO.getClassObjectType();
		}
		return classtype;
	}
	
	@Override
	public JavaObjectTypeModel getInterfaceType() {
		if (interfacetype == null) {
			interfacetype = javaAssemblyDAO.getInterfaceObjectType();
		}
		return interfacetype;
	}
	
	@Override
	public JavaObjectTypeModel getEnumType() {
		if (enumtype == null) {
			enumtype = javaAssemblyDAO.getEnumObjectType();
		}
		return enumtype;
	}
	
	@Override
	public JavaObjectTypeModel getAnnotationType() {
		if (annotationtype == null) {
			annotationtype = javaAssemblyDAO.getAnnotationType();
		}
		return annotationtype;
	}
	
	@Override
	public JavaObjectTypeModel getObjectTypeByID(Integer id) {
		JavaObjectTypeModel result = getAnnotationType();
		if (getPackageType().getPK().equals(id)) {
			result = getPackageType();
		} else if (getClassType().getPK().equals(id)) {
			result = getClassType();
		} else if (getInterfaceType().getPK().equals(id)) {
			result = getInterfaceType();
		} else if (getEnumType().getPK().equals(id)) {
			result = getEnumType();
		}
		
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeArgumentEntry getJavaExtendsArguments(JavaAssemblyModel javaAssembly) {
		JavaTypeModel extendsType = javaAssemblyDAO.getJavaAssemblyExtends(javaAssembly);
		if (extendsType != null) {
			return getJavaArgument(extendsType);
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TypeArgumentEntry> getJavaImplementsArguments(JavaAssemblyModel javaAssembly) {
		return javaAssemblyDAO.getJavaAssemblyImplements(javaAssembly).stream()
				.map(i -> getJavaArgument(i))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<JavaAssemblyModel> getImplementingAssemblies(JavaAssemblyModel javaAssembly) {
		return javaAssemblyDAO.getJavaAssemblyImplemented(javaAssembly);
	}
	
	@Override
	public List<JavaAssemblyModel> getImplementingAssemblies(JavaTypeModel javaType) {
		return javaAssemblyDAO.getJavaAssemblyImplemented(javaType);
	}
	
	@Override
	public JavaTypeModel getJavaType(JavaAssemblyModel javaAssembly, List<JavaTypeModel> typearguments) {
		List<JavaTypeModel> javaTypes = javaAssemblyDAO.getJavaTypes(javaAssembly, typearguments.size());

		return javaTypes.stream()
				.filter(jt -> {
					if (typearguments.isEmpty()) {
						return true;
					} else {
						List<JavaTypeModel> jta = javaAssemblyDAO.getJavaTypeArguments(jt);
						if (typearguments.size() == jta.size()) {
							return typearguments.size() == IntStream.range(0, typearguments.size())
									.map(c -> typearguments.get(c).equals(jta.get(c)) ? 1 : 0).sum();
						} else {
							return false;
						}
					}
				})
				.findFirst()
				.orElseGet(() -> javaTypeDAO.createJavaType(javaAssembly, typearguments));
	}
	
	@Override
	public TypeArgumentEntry getJavaArgument(JavaTypeModel javaType) {
		TypeArgumentEntry result = new TypeArgumentEntry();
		result.setType(javaAssemblyDAO.getAssembly(javaType.getJavaAssemblyID()));
		result.getArguments().addAll(javaTypeDAO.getTypeArguments(javaType));
		return result;
	}
	
	public JavaAssemblyDAO getJavaAssemblyDAO() {
		return javaAssemblyDAO;
	}

	public void setJavaAssemblyDAO(JavaAssemblyDAO javaAssemblyDAO) {
		this.javaAssemblyDAO = javaAssemblyDAO;
	}

	public JavaTypeDAO getJavaTypeDAO() {
		return javaTypeDAO;
	}

	public void setJavaTypeDAO(JavaTypeDAO javaTypeDAO) {
		this.javaTypeDAO = javaTypeDAO;
	}
}
