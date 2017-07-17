package org.eaSTars.adashboard.gui.resources;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Resources {

	public static final ImageIcon APPICON = new ImageIcon(Resources.class.getClassLoader().getResource("org/eaSTars/adashboard/gui/resources/dashboard.png"));
	
	public static final Image APPICON16 = APPICON.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
	
	public static final ImageIcon PACKAGEICON = new ImageIcon(Resources.class.getClassLoader().getResource("org/eaSTars/adashboard/gui/resources/packageicon.png"));
	
	public static final ImageIcon CLASSICON = new ImageIcon(Resources.class.getClassLoader().getResource("org/eaSTars/adashboard/gui/resources/classicon.png"));
	
	public static final String APP_NAME = "ADashboard";
}
