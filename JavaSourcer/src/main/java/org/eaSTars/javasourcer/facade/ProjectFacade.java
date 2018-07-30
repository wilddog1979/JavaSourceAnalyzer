package org.eaSTars.javasourcer.facade;

import java.util.List;

import javax.swing.JRadioButtonMenuItem;

import org.eaSTars.javasourcer.dto.CreateProjectDTO;

public interface ProjectFacade {

	public List<JRadioButtonMenuItem> getProjects();
	
	public JRadioButtonMenuItem createProject(CreateProjectDTO createProjectDTO);
	
}
