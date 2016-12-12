package org.eaSTars.sca.dao.impl;

import java.util.List;
import java.util.Optional;

import org.eaSTars.dblayer.dao.FilterEntry;
import org.eaSTars.dblayer.dao.impl.DefaultAbstractDBLayerDAO;
import org.eaSTars.sca.dao.JavaBodyDeclarationDAO;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaFieldModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaMethodParameterModel;
import org.eaSTars.sca.model.JavaThrowsModel;
import org.eaSTars.sca.model.JavaTypeModel;

public class DefaultJavaBodyDeclarationDAO extends DefaultAbstractDBLayerDAO implements JavaBodyDeclarationDAO {

	@Override
	public JavaFieldModel getJavaField(JavaAssemblyModel parentAssembly, JavaTypeModel javatpye, String name) {
		return queryModel(JavaFieldModel.class,
				new FilterEntry("parentAssemblyID", parentAssembly.getPK()),
				new FilterEntry("javaTypeID", javatpye.getPK()),
				new FilterEntry("name", name));
	}
	
	@Override
	public JavaFieldModel createJavaField(JavaAssemblyModel parentAssembly, int modifiers, JavaTypeModel javatpye, String name) {
		JavaFieldModel result = getJavaField(parentAssembly, javatpye, name);
		if (result == null) {
			result = new JavaFieldModel();
			result.setParentAssemblyID(parentAssembly.getPK());
			result.setModifiers(modifiers);
			result.setJavaTypeID(javatpye.getPK());
			result.setName(name);
			saveModel(result);
		}
		return result;
	}
	
	@Override
	public JavaMethodModel getJavaMethod(JavaAssemblyModel parentAssembly, JavaTypeModel javatpye, String name, int parametercount) {
		return queryModel(JavaMethodModel.class,
				new FilterEntry("parentAssemblyID", parentAssembly.getPK()),
				new FilterEntry("javaTypeID", Optional.ofNullable(javatpye).map(jt -> jt.getPK()).orElse(null)),
				new FilterEntry("name", name),
				new FilterEntry("parameterCount", parametercount));
	}
	
	@Override
	public List<JavaMethodModel> getJavaMethods(JavaAssemblyModel parentAssembly, String name, int parametercount) {
		return queryModelList(JavaMethodModel.class,
				new FilterEntry("parentAssemblyID", parentAssembly.getPK()),
				new FilterEntry("name", name),
				new FilterEntry("parameterCount", parametercount));
	}
	
	@Override
	public List<JavaMethodParameterModel> getJavaMethodParameters(JavaMethodModel javaMethod) {
		return queryModelList(JavaMethodParameterModel.class,
				new FilterEntry("javaMethodID", javaMethod.getPK()));
	}
	
	@Override
	public Optional<JavaMethodParameterModel> getJavaMethodParameter(JavaMethodModel javaMethod, int orderNumber) {
		return Optional.ofNullable(queryModel(JavaMethodParameterModel.class,
				new FilterEntry("javaMethodID", javaMethod.getPK()),
				new FilterEntry("orderNumber", orderNumber)));
	}
	
	@Override
	public JavaMethodParameterModel createJavaMethodParameter(JavaMethodModel javaMethod, String name, int orderNumber,
			JavaTypeModel javaType) {
		return getJavaMethodParameter(javaMethod, orderNumber)
				.orElseGet(() -> {
					JavaMethodParameterModel result = new JavaMethodParameterModel();
					result.setJavaMethodID(javaMethod.getPK());
					result.setOrderNumber(orderNumber);
					result.setName(name);
					result.setJavaTypeID(javaType.getPK());
					saveModel(result);
					return result;
				});
	}
	
	@Override
	public JavaMethodModel createMethod(JavaAssemblyModel parentAssembly, int modifiers, JavaTypeModel javatpye, String name, int parametercount) {
		JavaMethodModel result = new JavaMethodModel();
		result.setParentAssemblyID(parentAssembly.getPK());
		result.setModifiers(modifiers);
		result.setJavaTypeID(Optional.ofNullable(javatpye).map(jt -> jt.getPK()).orElse(null));
		result.setName(name);
		result.setParameterCount(parametercount);
		saveModel(result);
		return result;
	}
	
	@Override
	public Optional<JavaThrowsModel> getJavathrows(JavaMethodModel javamethod, JavaTypeModel javatype) {
		return Optional.ofNullable(queryModel(JavaThrowsModel.class,
				new FilterEntry("javaMethodID", javamethod.getPK()),
				new FilterEntry("javaTypeID", javatype.getPK())));
	}
	
	@Override
	public JavaThrowsModel createJavathrows(JavaMethodModel javamethod, JavaTypeModel javatype) {
		return getJavathrows(javamethod, javatype)
				.orElseGet(() -> {
					JavaThrowsModel result = new JavaThrowsModel();
					result.setJavaMethodID(javamethod.getPK());
					result.setJavaTypeID(javatype.getPK());
					saveModel(result);
					return result;
				});
	}
}
