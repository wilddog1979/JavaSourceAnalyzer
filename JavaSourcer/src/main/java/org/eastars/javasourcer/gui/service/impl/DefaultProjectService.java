package org.eastars.javasourcer.gui.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JRadioButtonMenuItem;

import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.service.JavaSourcerDataService;
import org.eastars.javasourcer.gui.dto.ProjectDTO;
import org.eastars.javasourcer.gui.exception.JavaSourcerProjectAlreadyExistsException;
import org.eastars.javasourcer.gui.service.ProjectService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
public class DefaultProjectService implements ProjectService {

	private static final Comparator<String> STRINGCOMPARATOR = Comparator.comparing(String::toString);
	
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
		
		dataService.getJavaSourceProject(originalName)
		.ifPresent(jsp -> {
			boolean dirty = false;
			if (STRINGCOMPARATOR.compare(jsp.getName(), newValue.getName()) != 0) {
				if (jsp.getId() != dataService.getJavaSourceProject(newValue.getName()).get().getId()) {
					throw new JavaSourcerProjectAlreadyExistsException(newValue.getName());
				}
				jsp.setName(newValue.getName());
				dirty = true;
			}
			if (STRINGCOMPARATOR.compare(jsp.getBasedir(), newValue.getBasedir()) != 0) {
				jsp.setBasedir(newValue.getBasedir());
				dirty = true;
			}
			
			dirty |= dataService.updateSourceFolders(jsp, newValue.getSourceFolders()) |
					dataService.updateJavaLibraries(jsp, newValue.getLibraries());
			
			if (dirty) {
				dataService.save(jsp);
			}
		});
	}
	
}
