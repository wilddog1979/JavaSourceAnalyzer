package org.eaSTars.adashboard.converter.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import org.eaSTars.adashboard.gui.dto.JavaMethodDefinitionView;
import org.eaSTars.adashboard.gui.dto.JavaTypeArgumentView;
import org.eaSTars.adashboard.gui.dto.JavaTypeParameterView;
import org.eaSTars.adashboard.gui.dto.JavaVariableDefinition;
import org.eaSTars.adashboard.service.JavaAssemblyService;
import org.eaSTars.adashboard.service.JavaBodyDeclarationService;
import org.eaSTars.adashboard.service.JavaTypeService;
import org.eaSTars.sca.model.JavaMethodModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

public class DefaultMethodDefinitionConverter implements Converter<JavaMethodModel, JavaMethodDefinitionView> {

	private JavaAssemblyService javaAssemblyService;
	
	private JavaTypeService javaTypeService;
	
	private JavaBodyDeclarationService javaBodyDeclarationService;
	
	@Autowired
	@Lazy
	private ConversionService conversionService;
	
	@Override
	public JavaMethodDefinitionView convert(JavaMethodModel source) {
		JavaMethodDefinitionView target = new JavaMethodDefinitionView();

		target.addTypeParameters(javaTypeService.getJavaMethodTypeParameters(source).stream()
				.map(tp -> conversionService.convert(tp, JavaTypeParameterView.class))
				.collect(Collectors.toList()));
		
		Optional.ofNullable(javaTypeService.getJavaType(source.getJavaTypeID()))
		.ifPresent(t -> target.setType(conversionService.convert(this.javaAssemblyService.getJavaArgument(t), JavaTypeArgumentView.class)));
		
		target.setName(source.getName());

		target.addParameters(javaBodyDeclarationService.getJavaMethodParameters(source).stream()
				.map(mp -> {
					JavaVariableDefinition target2 = new JavaVariableDefinition();
					Optional.ofNullable(javaTypeService.getJavaType(mp.getJavaTypeID()))
					.ifPresent(t -> target2.setType(conversionService.convert(this.javaAssemblyService.getJavaArgument(t), JavaTypeArgumentView.class)));
					
					target2.setName(mp.getName());

					return target2;
				})
				.collect(Collectors.toList()));
		
		return target;
	}

	public JavaAssemblyService getJavaAssemblyService() {
		return javaAssemblyService;
	}

	public void setJavaAssemblyService(JavaAssemblyService javaAssemblyService) {
		this.javaAssemblyService = javaAssemblyService;
	}

	public JavaTypeService getJavaTypeService() {
		return javaTypeService;
	}

	public void setJavaTypeService(JavaTypeService javaTypeService) {
		this.javaTypeService = javaTypeService;
	}

	public JavaBodyDeclarationService getJavaBodyDeclarationService() {
		return javaBodyDeclarationService;
	}

	public void setJavaBodyDeclarationService(JavaBodyDeclarationService javaBodyDeclarationService) {
		this.javaBodyDeclarationService = javaBodyDeclarationService;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

}
