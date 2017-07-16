package org.eaSTars.adashboard.controller.impl;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PlantUMLFileFilter extends FileFilter {

	public static final String EXTENSION = ".puml";
	
	@Override
	public boolean accept(File f) {
		return f.isFile() && f.getName().toLowerCase().endsWith(EXTENSION);
	}

	@Override
	public String getDescription() {
		return String.format("PlantUML script (%s)", EXTENSION);
	}

}
