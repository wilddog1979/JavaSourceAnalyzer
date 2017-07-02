package org.eaSTars.adashboard.controller.impl;

import org.eaSTars.adashboard.controller.JavaSequenceDiagramController;
import org.eaSTars.adashboard.gui.dto.JavaSequenceDiagramView;
import org.eaSTars.adashboard.service.JavaSequenceService;
import org.springframework.core.convert.ConversionService;

public class DefaultJavaSequenceDiagramController implements JavaSequenceDiagramController {

	private JavaSequenceService javaSequenceService;
	
	private ConversionService conversionService;
	
	@Override
	public JavaSequenceDiagramView getSequenceView(Integer methodid) {
		return conversionService.convert(javaSequenceService.generateMethodSequence(methodid), JavaSequenceDiagramView.class);
	}

	public JavaSequenceService getJavaSequenceService() {
		return javaSequenceService;
	}

	public void setJavaSequenceService(JavaSequenceService javaSequenceService) {
		this.javaSequenceService = javaSequenceService;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
