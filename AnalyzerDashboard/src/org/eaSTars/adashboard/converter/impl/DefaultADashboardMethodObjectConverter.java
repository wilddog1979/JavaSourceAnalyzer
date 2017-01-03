package org.eaSTars.adashboard.converter.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eaSTars.adashboard.gui.dto.ADashboardObjectType;
import org.eaSTars.adashboard.gui.dto.ADashboardObjectView;
import org.eaSTars.adashboard.service.JavaAssemblyService;
import org.eaSTars.adashboard.service.JavaBodyDeclarationService;
import org.eaSTars.adashboard.service.JavaTypeService;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaTypeModel;
import org.springframework.core.convert.converter.Converter;

public class DefaultADashboardMethodObjectConverter implements Converter<JavaMethodModel, ADashboardObjectView> {

	private JavaAssemblyService javaAssemblyService;
	
	private JavaBodyDeclarationService javaBodyDeclarationService;
	
	private JavaTypeService javaTypeService;
	
	@Override
	public ADashboardObjectView convert(JavaMethodModel source) {
		ADashboardObjectView target = new ADashboardObjectView();
		target.setId(source.getPK());
		target.setName(source.getName());
		target.setType(ADashboardObjectType.METHOD);

		target.setTooltip(javaBodyDeclarationService.getJavaMethodParameters(source).stream()
				.map(mp -> Optional.ofNullable(javaTypeService.getJavaType(mp.getJavaTypeID()))
						.map(t -> getTypeDescriptor(t) + " " + mp.getName())
						.orElseGet(() -> ""))
				.collect(Collectors.joining(", ")));
		
		return target;
	}
	
	private String getTypeDescriptor(JavaTypeModel javaType) {
		JavaAssemblyModel jam = javaAssemblyService.getJavaAssembly(javaType.getJavaAssemblyID());
		List<JavaTypeModel> arguments = javaTypeService.getTypeArguments(javaType);
		
		return jam.getName() + (arguments.isEmpty() ? "" :
			arguments.stream()
			.map(arg -> getTypeDescriptor(arg))
			.collect(Collectors.joining(", ", "<", ">")));
	}

	public JavaAssemblyService getJavaAssemblyService() {
		return javaAssemblyService;
	}

	public void setJavaAssemblyService(JavaAssemblyService javaAssemblyService) {
		this.javaAssemblyService = javaAssemblyService;
	}

	public JavaBodyDeclarationService getJavaBodyDeclarationService() {
		return javaBodyDeclarationService;
	}

	public void setJavaBodyDeclarationService(JavaBodyDeclarationService javaBodyDeclarationService) {
		this.javaBodyDeclarationService = javaBodyDeclarationService;
	}

	public JavaTypeService getJavaTypeService() {
		return javaTypeService;
	}

	public void setJavaTypeService(JavaTypeService javaTypeService) {
		this.javaTypeService = javaTypeService;
	}
	
}
