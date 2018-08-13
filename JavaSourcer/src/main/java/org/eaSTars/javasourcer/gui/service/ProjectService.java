package org.eaSTars.javasourcer.gui.service;

import java.util.List;
import java.util.Optional;

import javax.swing.JRadioButtonMenuItem;

import org.eaSTars.javasourcer.gui.dto.ProjectDTO;

public interface ProjectService {

	public List<JRadioButtonMenuItem> getProjects();
	
	public Optional<ProjectDTO> getProject(String name);
	
	public void updateProject(String originalName, ProjectDTO newValue);
	
	public JRadioButtonMenuItem createProject(ProjectDTO createProjectDTO);
	
}
