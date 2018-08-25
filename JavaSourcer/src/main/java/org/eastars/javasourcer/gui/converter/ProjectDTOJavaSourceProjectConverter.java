package org.eastars.javasourcer.gui.converter;

import java.util.Optional;

import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.service.JavaSourcerDataService;
import org.eastars.javasourcer.gui.dto.ProjectDTO;
import org.eastars.javasourcer.gui.exception.JavaSourcerProjectAlreadyExistsException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProjectDTOJavaSourceProjectConverter implements Converter<ProjectDTO, JavaSourceProject> {

	private JavaSourcerDataService javaSourcerDataService;

	public ProjectDTOJavaSourceProjectConverter(
			JavaSourcerDataService javaSourcerDataService) {
		this.javaSourcerDataService = javaSourcerDataService;
	}
	
	@Override
	public JavaSourceProject convert(ProjectDTO source) {
		Optional<JavaSourceProject> javaSourceProject = javaSourcerDataService.getJavaSourceProject(source.getName());
		
		if (javaSourceProject.isPresent()) {
			throw new JavaSourcerProjectAlreadyExistsException(source.getName());
		}
		
		JavaSourceProject target = new JavaSourceProject();
		target.setName(source.getName());
		target.setBasedir(source.getBasedir());
		
		javaSourcerDataService.save(target);
		
		return target;
	}

}
