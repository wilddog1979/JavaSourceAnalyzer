package org.eaSTars.adashboard.converter.impl;

import org.eaSTars.adashboard.gui.dto.JavaAssemblyDefinitionView;
import org.eaSTars.adashboard.gui.dto.JavaAssemblyFullView;
import org.eaSTars.adashboard.gui.dto.JavaFieldView;
import org.eaSTars.adashboard.gui.dto.JavaMethodDefinitionView;
import org.eaSTars.adashboard.service.JavaBodyDeclarationService;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

public class DefaultJavaAssemblyFullViewConverter implements Converter<JavaAssemblyModel, JavaAssemblyFullView> {

	private JavaBodyDeclarationService javaBodyDeclarationService;
	
	@Autowired
	@Lazy
	private ConversionService conversionService;
	
	@Override
	public JavaAssemblyFullView convert(JavaAssemblyModel source) {
		JavaAssemblyFullView target = new JavaAssemblyFullView();

		target.addEntry(conversionService.convert(source, JavaAssemblyDefinitionView.class));
		
		javaBodyDeclarationService.getFields(source)
		.forEach(f -> target.addEntry(conversionService.convert(f, JavaFieldView.class)));
		
		javaBodyDeclarationService.getMethods(source)
		.forEach(m -> target.addEntry(conversionService.convert(m, JavaMethodDefinitionView.class)));
		
		return target;
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
