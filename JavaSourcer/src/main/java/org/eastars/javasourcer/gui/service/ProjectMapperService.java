package org.eastars.javasourcer.gui.service;

import java.util.List;

import javax.swing.JRadioButtonMenuItem;

import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.gui.dto.ModuleDTO;
import org.eastars.javasourcer.gui.dto.ProjectDTO;

public interface ProjectMapperService {

	public JRadioButtonMenuItem mapJRadioButtonMenuItem(JavaSourceProject javaSourceProject);
	
	public JRadioButtonMenuItem mapJRadioButtonMenuItem(ProjectDTO dto);
	
	public void mapJavaSourceProject(JavaSourceProject javaSourceProject, ProjectDTO dto);

	public void mapSourceModules(JavaSourceProject javaSourceProject, List<ModuleDTO> modules);

	public void mapJavaLibraries(JavaSourceProject javaSourceProject, List<String> libraries);
	
}
