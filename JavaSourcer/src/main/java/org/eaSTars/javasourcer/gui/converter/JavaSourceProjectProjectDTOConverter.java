package org.eaSTars.javasourcer.gui.converter;

import org.eaSTars.javasourcer.data.model.JavaSourceProject;
import org.eaSTars.javasourcer.gui.dto.ProjectDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class JavaSourceProjectProjectDTOConverter implements Converter<JavaSourceProject, ProjectDTO> {

	@Override
	public ProjectDTO convert(JavaSourceProject source) {
		ProjectDTO target = new ProjectDTO();
		
		target.setName(source.getName());
		target.setBasedir(source.getBasedir());
		
		return target;
	}

}
