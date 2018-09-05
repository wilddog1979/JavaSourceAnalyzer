package org.eastars.javasourcer.gui.controller.impl;

import org.eastars.javasourcer.gui.controller.DialogControllers;
import org.eastars.javasourcer.gui.controller.JavaSourcerDataInputDialog;
import org.eastars.javasourcer.gui.controller.JavaSourcerDialog;
import org.eastars.javasourcer.gui.dto.ProjectDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DefaultDialogControllers implements DialogControllers {

	private JavaSourcerDialog aboutDialog;
	
	private JavaSourcerDialog preferencesDialog;

	private JavaSourcerDataInputDialog<ProjectDTO, String> projectDialog;
	
	public DefaultDialogControllers(
			@Qualifier("aboutdailogcontroller") JavaSourcerDialog aboutDialog,
			@Qualifier("preferencesdailogcontroller") JavaSourcerDialog preferencesDialog,
			@Qualifier("projectdailogcontroller") JavaSourcerDataInputDialog<ProjectDTO, String> projectDialog) {
		this.aboutDialog = aboutDialog;
		this.preferencesDialog = preferencesDialog;
		this.projectDialog = projectDialog;
	}

	/**
	 * @return the aboutDialog
	 */
	@Override
	public JavaSourcerDialog getAboutDialog() {
		return aboutDialog;
	}

	/**
	 * @return the preferencesDialog
	 */
	@Override
	public JavaSourcerDialog getPreferencesDialog() {
		return preferencesDialog;
	}

	/**
	 * @return the projectDialog
	 */
	@Override
	public JavaSourcerDataInputDialog<ProjectDTO, String> getProjectDialog() {
		return projectDialog;
	}
	
}
