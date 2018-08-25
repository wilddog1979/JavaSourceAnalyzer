package org.eastars.javasourcer.gui.controller;

import javax.swing.JPanel;

public interface DialogPanelController {

	public JPanel getPanel();
	
	public void initializeContent();
	
	public boolean saveContent();
	
}
