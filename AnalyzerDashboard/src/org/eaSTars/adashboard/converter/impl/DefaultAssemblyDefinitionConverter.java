package org.eaSTars.adashboard.converter.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import org.eaSTars.adashboard.gui.dto.JavaAssemblyDefinitionView;
import org.eaSTars.adashboard.gui.dto.JavaTypeArgumentView;
import org.eaSTars.adashboard.gui.dto.JavaTypeParameterView;
import org.eaSTars.adashboard.service.JavaAssemblyService;
import org.eaSTars.adashboard.service.JavaTypeService;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

public class DefaultAssemblyDefinitionConverter implements Converter<JavaAssemblyModel, JavaAssemblyDefinitionView> {

	private JavaAssemblyService javaAssemblyService;
	
	private JavaTypeService javaTypeService;
	
	@Autowired
	@Lazy
	private ConversionService conversionService;
	
	@Override
	public JavaAssemblyDefinitionView convert(JavaAssemblyModel source) {
		JavaAssemblyDefinitionView target = new JavaAssemblyDefinitionView();
		
		target.setTypeName(javaAssemblyService.getObjectTypeByID(source.getJavaObjectTypeID()).getName());
		target.setName(source.getName());
		target.setTypeParameters(javaTypeService.getJavaAssemblyTypeParameters(source).stream()
				.map(tp -> conversionService.convert(tp, JavaTypeParameterView.class))
				.collect(Collectors.toList()));

		Optional.ofNullable(javaAssemblyService.getJavaExtendsArguments(source))
		.ifPresent(ext -> target.setExtends(conversionService.convert(ext, JavaTypeArgumentView.class)));

		target.setImplements(javaAssemblyService.getJavaImplementsArguments(source).stream()
				.map(i -> conversionService.convert(i, JavaTypeArgumentView.class))
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

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

}
