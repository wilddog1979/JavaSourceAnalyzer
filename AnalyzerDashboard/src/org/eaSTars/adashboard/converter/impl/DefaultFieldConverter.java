package org.eaSTars.adashboard.converter.impl;

import java.util.Optional;

import org.eaSTars.adashboard.gui.dto.JavaFieldView;
import org.eaSTars.adashboard.gui.dto.JavaTypeArgumentView;
import org.eaSTars.adashboard.service.JavaAssemblyService;
import org.eaSTars.adashboard.service.JavaTypeService;
import org.eaSTars.sca.model.JavaFieldModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

public class DefaultFieldConverter implements Converter<JavaFieldModel, JavaFieldView> {

	private JavaAssemblyService javaAssemblyService;
	
	private JavaTypeService javaTypeService;
	
	@Autowired
	@Lazy
	private ConversionService conversionService;
	
	@Override
	public JavaFieldView convert(JavaFieldModel source) {
		JavaFieldView target = new JavaFieldView();
		
		target.setName(source.getName());
		
		Optional.ofNullable(javaTypeService.getJavaType(source.getJavaTypeID()))
		.ifPresent(t -> target.setType(conversionService.convert(this.javaAssemblyService.getJavaArgument(t), JavaTypeArgumentView.class)));
		
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
