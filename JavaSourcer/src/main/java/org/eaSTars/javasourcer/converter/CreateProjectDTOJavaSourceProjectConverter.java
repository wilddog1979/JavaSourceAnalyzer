package org.eaSTars.javasourcer.converter;

import java.util.Optional;

import org.eaSTars.javasourcer.dto.CreateProjectDTO;
import org.eaSTars.javasourcer.exception.JavaSourcerProjectAlreadyExistsException;
import org.eaSTars.javasourcer.model.JavaSourceProject;
import org.eaSTars.javasourcer.repository.JavaSourceProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CreateProjectDTOJavaSourceProjectConverter implements Converter<CreateProjectDTO, JavaSourceProject> {

	@Autowired
	private JavaSourceProjectRepository javaSourceProjectRepo;
	
	@Override
	public JavaSourceProject convert(CreateProjectDTO source) {
		Optional<JavaSourceProject> javaSourceProject = javaSourceProjectRepo.findByName(source.getName());
		
		if (javaSourceProject.isPresent()) {
			throw new JavaSourcerProjectAlreadyExistsException(source.getName());
		}
		
		JavaSourceProject target = new JavaSourceProject();
		target.setName(source.getName());
		
		javaSourceProjectRepo.save(target);
		
		return target;
	}

}
