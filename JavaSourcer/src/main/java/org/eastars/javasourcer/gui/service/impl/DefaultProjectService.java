package org.eastars.javasourcer.gui.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JRadioButtonMenuItem;

import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.service.JavaSourcerDataService;
import org.eastars.javasourcer.gui.dto.ProjectDTO;
import org.eastars.javasourcer.gui.service.ProjectMapperService;
import org.eastars.javasourcer.gui.service.ProjectService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class DefaultProjectService implements ProjectService {

	private ConversionService conversionService;
	
	private JavaSourcerDataService dataService;
	
	private ProjectMapperService projectMapperService;
	
	public DefaultProjectService(
			@Qualifier("javasourcerconversionservice") ConversionService conversionService,
			JavaSourcerDataService dataService,
			ProjectMapperService projectMapperService) {
		this.conversionService = conversionService;
		this.dataService = dataService;
		this.projectMapperService = projectMapperService;
	}
	
	@Override
	public List<JRadioButtonMenuItem> getProjects() {
		return dataService.getJavaSourceProjects().stream()
				.map(p -> projectMapperService.mapJRadioButtonMenuItem(p))
				.collect(Collectors.toList());
	}
	
	@Override
	public Optional<ProjectDTO> getProject(String name) {
		return dataService.getJavaSourceProject(name)
				.map(jsp -> conversionService.convert(jsp, ProjectDTO.class));
	}
	
	@Override
	public JRadioButtonMenuItem createProject(ProjectDTO createProjectDTO) {
		JavaSourceProject javaSourceProject = conversionService.convert(createProjectDTO, JavaSourceProject.class);
		return conversionService.convert(javaSourceProject, JRadioButtonMenuItem.class);
	}

	@Override
	public void updateProject(String originalName, ProjectDTO newValue) {
		JavaSourceProject javaSourceProject = dataService.getJavaSourceProject(originalName)
				.orElseGet(JavaSourceProject::new);
		
		projectMapperService.mapJavaSourceProject(javaSourceProject, newValue);
		
		projectMapperService.mapSourceModules(javaSourceProject, newValue.getModules());
		
		projectMapperService.mapJavaLibraries(javaSourceProject, newValue.getLibraries());
		
		dataService.save(javaSourceProject);
	}
	
}
