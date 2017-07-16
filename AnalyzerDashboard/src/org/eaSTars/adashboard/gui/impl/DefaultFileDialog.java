package org.eaSTars.adashboard.gui.impl;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.eaSTars.adashboard.gui.FileDialog;

public class DefaultFileDialog implements FileDialog {

	@Override
	public File showSaveDialog(Component parent, FileFilter filter) {
		JFileChooser fileChooser = new JFileChooser(".");
		fileChooser.setFileFilter(filter);
		if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		} else {
			return null;
		}
	}

	
}
