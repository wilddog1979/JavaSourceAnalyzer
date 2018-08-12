package org.eaSTars.javasourcer.gui.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JRadioButtonMenuItem;

import org.eaSTars.javasourcer.data.model.JavaSourceProject;
import org.eaSTars.javasourcer.data.service.JavaSourcerDataService;
import org.eaSTars.javasourcer.gui.dto.CreateProjectDTO;
import org.eaSTars.javasourcer.gui.service.ProjectService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class DefaultProjectService implements ProjectService {

	private ConversionService conversionService;
	
	private JavaSourcerDataService dataService;
	
	public DefaultProjectService(
			@Qualifier("javasourcerconversionservice") ConversionService conversionService,
			JavaSourcerDataService dataService) {
		this.conversionService = conversionService;
		this.dataService = dataService;
	}
	
	@Override
	public List<JRadioButtonMenuItem> getProjects() {
		return dataService.getJavaSourceProjects().stream()
				.map(p -> conversionService.convert(p, JRadioButtonMenuItem.class))
				.collect(Collectors.toList());
	}
	
	@Override
	public JRadioButtonMenuItem createProject(CreateProjectDTO createProjectDTO) {
		JavaSourceProject javaSourceProject = conversionService.convert(createProjectDTO, JavaSourceProject.class);
		return conversionService.convert(javaSourceProject, JRadioButtonMenuItem.class);
	}

}
