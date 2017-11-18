package org.eaSTars.adashboard.converter.impl;

import org.eaSTars.adashboard.gui.dto.ADashboardObjectType;
import org.eaSTars.adashboard.gui.dto.ADashboardObjectView;
import org.eaSTars.adashboard.service.JavaAssemblyService;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.springframework.core.convert.converter.Converter;

public class DefaultADashboardAssemblyObjectViewConverter implements Converter<JavaAssemblyModel, ADashboardObjectView> {

	private JavaAssemblyService javaAssemblyService;
	
	@Override
	public ADashboardObjectView convert(JavaAssemblyModel source) {
		ADashboardObjectView target = new ADashboardObjectView();
		
		target.setId(source.getPK());
		target.setName(source.getName());
		if (source.getJavaObjectTypeID().equals(javaAssemblyService.getPackageType().getPK())) {
			target.setType(ADashboardObjectType.PACKAGE);
		} else if (source.getJavaObjectTypeID().equals(javaAssemblyService.getClassType().getPK())) {
			target.setType(ADashboardObjectType.CLASS);
		} else if (source.getJavaObjectTypeID().equals(javaAssemblyService.getInterfaceType().getPK())) {
			target.setType(ADashboardObjectType.INTERFACE);
		} else if (source.getJavaObjectTypeID().equals(javaAssemblyService.getEnumType().getPK())) {
			target.setType(ADashboardObjectType.ENUM);
		} else {
			target.setType(ADashboardObjectType.ANNOTATION);
		}
		
		return target;
	}

	public JavaAssemblyService getJavaAssemblyService() {
		return javaAssemblyService;
	}

	public void setJavaAssemblyService(JavaAssemblyService javaAssemblyService) {
		this.javaAssemblyService = javaAssemblyService;
	}

}
