package org.eastars.javasourcer.gui.converter;

import java.util.stream.Collectors;

import org.eastars.javasourcer.data.model.JavaLibrary;
import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.model.SourceFolder;
import org.eastars.javasourcer.gui.dto.ProjectDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class JavaSourceProjectProjectDTOConverter implements Converter<JavaSourceProject, ProjectDTO> {

	@Override
	public ProjectDTO convert(JavaSourceProject source) {
		ProjectDTO target = new ProjectDTO();
		
		target.setName(source.getName());
		target.setBasedir(source.getBasedir());
		
		target.setSourceFolders(source.getSourceFolders().stream()
				.map(SourceFolder::getRelativedir)
				.collect(Collectors.toList()));
		
		target.setLibraries(source.getJavaLibraries().stream()
				.map(JavaLibrary::getName)
				.collect(Collectors.toList()));
		
		return target;
	}

}