package org.eaSTars.adashboard.gui;

import java.awt.Component;
import java.io.File;

import javax.swing.filechooser.FileFilter;

public interface FileDialog {

	public File showSaveDialog(Component parent, FileFilter filter);
}
