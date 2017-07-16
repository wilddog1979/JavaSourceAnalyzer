package org.eaSTars.adashboard.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PNGFileFilter extends FileFilter {

	public static final String EXTENSION = ".png";
	
	@Override
	public boolean accept(File f) {
		return f.isFile() && f.getName().toLowerCase().endsWith(EXTENSION);
	}

	@Override
	public String getDescription() {
		return String.format("PNG file (%s)", EXTENSION);
	}

}
