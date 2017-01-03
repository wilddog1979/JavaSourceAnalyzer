package org.eaSTars.adashboard.converter.impl;

import java.util.stream.Collectors;

import org.eaSTars.adashboard.gui.MainFrameDelegate;
import org.eaSTars.adashboard.gui.dto.JavaTypeArgumentView;
import org.eaSTars.adashboard.service.JavaAssemblyService;
import org.eaSTars.sca.dao.TypeArgumentEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

public class DefaultJavaTypeArgumentConverter implements Converter<TypeArgumentEntry, JavaTypeArgumentView> {

	private JavaAssemblyService javaAssemblyService;
	
	@Autowired
	@Lazy
	private MainFrameDelegate delegate;
	
	@Autowired
	@Lazy
	private ConversionService conversionService;
	
	@Override
	public JavaTypeArgumentView convert(TypeArgumentEntry source) {
		JavaTypeArgumentView target = new JavaTypeArgumentView(delegate);
		
		target.setAssemblyLink(source.getType().getName(), source.getType().getPK());
		
		target.setArguments(source.getArguments().stream()
				.map(arg -> conversionService.convert(javaAssemblyService.getJavaArgument(arg), JavaTypeArgumentView.class))
				.collect(Collectors.toList()));
		
		return target;
	}

	public JavaAssemblyService getJavaAssemblyService() {
		return javaAssemblyService;
	}

	public void setJavaAssemblyService(JavaAssemblyService javaAssemblyService) {
		this.javaAssemblyService = javaAssemblyService;
	}

	public MainFrameDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(MainFrameDelegate delegate) {
		this.delegate = delegate;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

}
