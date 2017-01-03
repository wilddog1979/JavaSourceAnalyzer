package org.eaSTars.adashboard.converter.impl;

import org.eaSTars.adashboard.gui.dto.JavaMethodDefinitionView;
import org.eaSTars.adashboard.gui.dto.JavaMethodView;
import org.eaSTars.sca.model.JavaMethodModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

public class DefaultMethodConverter implements Converter<JavaMethodModel, JavaMethodView> {

	@Autowired
	@Lazy
	private ConversionService conversionService;
	
	@Override
	public JavaMethodView convert(JavaMethodModel source) {
		JavaMethodView target = new JavaMethodView();
		
		target.addEntry(conversionService.convert(source, JavaMethodDefinitionView.class));
		
		return target;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

}
