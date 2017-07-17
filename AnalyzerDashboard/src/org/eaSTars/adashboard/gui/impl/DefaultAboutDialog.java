package org.eaSTars.adashboard.gui.impl;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.eaSTars.adashboard.gui.ADashboardDialog;
import org.eaSTars.adashboard.gui.resources.Resources;

public class DefaultAboutDialog implements ADashboardDialog {
	
	@Override
	public boolean showDialog() {
		JLabel label = new JLabel("About ADashboard", new ImageIcon(Resources.APPICON.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH)), JLabel.LEFT);
		JOptionPane.showMessageDialog(null, label, "About ADashboard", JOptionPane.PLAIN_MESSAGE);
		return true;
	}

}
