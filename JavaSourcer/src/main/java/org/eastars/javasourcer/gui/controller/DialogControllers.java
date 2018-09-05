package org.eastars.javasourcer.gui.controller;

import org.eastars.javasourcer.gui.dto.ProjectDTO;

public interface DialogControllers {

	public JavaSourcerDialog getAboutDialog();
	
	public JavaSourcerDialog getPreferencesDialog();
	
	public JavaSourcerDataInputDialog<ProjectDTO, String> getProjectDialog();
	
}
