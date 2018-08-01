package org.eaSTars.javasourcer.controller.impl;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.eaSTars.javasourcer.configuration.ApplicationResources;
import org.eaSTars.javasourcer.controller.JavaSourcerDialog;
import org.springframework.stereotype.Component;

@Component("aboutdailogcontroller")
public class DefaultJavaSourcerAboutDialogController implements JavaSourcerDialog {

	@Override
	public boolean showDialog(java.awt.Component parent) {
		JLabel label = new JLabel("About Java Sourcer", new ImageIcon(ApplicationResources.APPICON16), JLabel.LEFT);
		JOptionPane.showMessageDialog(parent, label, "About", JOptionPane.PLAIN_MESSAGE);
		return true;
	}

}
