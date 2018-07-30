package org.eaSTars.javasourcer.facade.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.swing.JRadioButtonMenuItem;

import org.eaSTars.javasourcer.dto.CreateProjectDTO;
import org.eaSTars.javasourcer.facade.ProjectFacade;
import org.eaSTars.javasourcer.model.JavaSourceProject;
import org.eaSTars.javasourcer.service.JavaSourcerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
public class DefaultProjectFacade implements ProjectFacade {

	@Resource(name="javasourcerconversionservice")
	private ConversionService conversionService;
	
	@Autowired
	private JavaSourcerDataService dataService;
	
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
