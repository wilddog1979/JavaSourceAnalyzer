package org.eaSTars.adashboard.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public abstract class AbstractFileFilter extends FileFilter {

	protected abstract String getFileTypeName();
	
	protected abstract String getExtension();
	
	@Override
	public boolean accept(File f) {
		return (f.isFile() && f.getName().toLowerCase().endsWith(getExtension())) || f.isDirectory();
	}
	
	@Override
	public String getDescription() {
		return String.format("%s (*%s)", getFileTypeName(), getExtension());
	}
}
