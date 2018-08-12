package org.eaSTars.javasourcer.gui.converter;

import java.util.Optional;

import org.eaSTars.javasourcer.data.model.JavaSourceProject;
import org.eaSTars.javasourcer.data.service.JavaSourcerDataService;
import org.eaSTars.javasourcer.gui.dto.CreateProjectDTO;
import org.eaSTars.javasourcer.gui.exception.JavaSourcerProjectAlreadyExistsException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CreateProjectDTOJavaSourceProjectConverter implements Converter<CreateProjectDTO, JavaSourceProject> {

	private JavaSourcerDataService javaSourcerDataService;

	public CreateProjectDTOJavaSourceProjectConverter(
			JavaSourcerDataService javaSourcerDataService) {
		this.javaSourcerDataService = javaSourcerDataService;
	}
	
	@Override
	public JavaSourceProject convert(CreateProjectDTO source) {
		Optional<JavaSourceProject> javaSourceProject = javaSourcerDataService.getJavaSourceProject(source.getName());
		
		if (javaSourceProject.isPresent()) {
			throw new JavaSourcerProjectAlreadyExistsException(source.getName());
		}
		
		JavaSourceProject target = new JavaSourceProject();
		target.setName(source.getName());
		
		javaSourcerDataService.save(target);
		
		return target;
	}

}
