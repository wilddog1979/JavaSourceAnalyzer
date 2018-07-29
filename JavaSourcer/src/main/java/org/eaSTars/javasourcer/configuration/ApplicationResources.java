package org.eaSTars.javasourcer.configuration;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ApplicationResources {

	public static final ImageIcon APPICON = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/dashboard.png"));
	
	public static final Image APPICON16 = APPICON.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
	
	public static final ImageIcon PACKAGEICON = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/packageicon.png"));
	
	public static final ImageIcon CLASSICON = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/classicon.png"));
	
	public static final String APP_NAME = "Java Sourcer";
}
