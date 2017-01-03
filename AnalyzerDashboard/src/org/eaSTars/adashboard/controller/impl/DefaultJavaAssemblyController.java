package org.eaSTars.adashboard.controller.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eaSTars.adashboard.controller.JavaAssemblyController;
import org.eaSTars.adashboard.gui.dto.ADashboardObjectView;
import org.eaSTars.adashboard.gui.dto.JavaAssemblyFullView;
import org.eaSTars.adashboard.service.JavaAssemblyService;
import org.eaSTars.adashboard.service.JavaBodyDeclarationService;
import org.springframework.core.convert.ConversionService;

public class DefaultJavaAssemblyController implements JavaAssemblyController {

	private JavaAssemblyService javaAssemblyService;
	
	private JavaBodyDeclarationService javaBodyDeclarationService;
	
	private ConversionService conversionService;
	
	@Override
	public List<ADashboardObjectView> getChildAssemblies(Integer parentId) {
		return javaAssemblyService.getChildAssemblies(parentId).stream()
				.sorted((a1, a2) -> a1.getName().compareTo(a2.getName()))
				.map(a -> conversionService.convert(a, ADashboardObjectView.class))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<ADashboardObjectView> getAssemblyMethods(Integer parentId) {
		return javaBodyDeclarationService.getMethods(parentId).stream()
				.sorted((m1, m2) -> m1.getName().compareTo(m2.getName()))
				.map(m -> conversionService.convert(m, ADashboardObjectView.class))
				.collect(Collectors.toList());
	}
	
	@Override
	public JavaAssemblyFullView getAssemblyFullView(Integer id) {
		return Optional.ofNullable(javaAssemblyService.getJavaAssembly(id))
				.map(a -> conversionService.convert(a, JavaAssemblyFullView.class))
				.orElseGet(() -> null);
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

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

}
