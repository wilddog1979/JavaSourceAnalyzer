package org.eaSTars.javasourcer.gui.service;

import java.util.List;

import javax.swing.JRadioButtonMenuItem;

import org.eaSTars.javasourcer.gui.dto.CreateProjectDTO;

public interface ProjectService {

	public List<JRadioButtonMenuItem> getProjects();
	
	public JRadioButtonMenuItem createProject(CreateProjectDTO createProjectDTO);
	
}
