package org.eaSTars.javasourcer.configuration;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ApplicationResources {

	public static class ResourceBundle {
		public static final String MAIN_MENU_SWITCH = "main.menu.switch";
		
		public static final String MAIN_MENU_ADD = "main.menu.add";
		
		public static final String MAIN_MENU_PROJECT = "main.menu.project";
		
		public static final String MAIN_MENU_PROPERTIES = "main.menu.properties";
		
		public static final String MAIN_MENU_SETTINGS = "main.menu.settings";
		
		public static final String MAIN_MENU_PREFERENCES = "main.menu.preferences";
		
		public static final String MAIN_MENU_HELP = "main.menu.help";
		
		public static final String MAIN_MENU_ABOUT = "main.menu.about";
	}
	
	public static final ImageIcon APPICON = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/dashboard.png"));
	
	public static final Image APPICON16 = APPICON.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
	
	public static final ImageIcon PACKAGEICON = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/packageicon.png"));
	
	public static final ImageIcon CLASSICON = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/classicon.png"));
	
	public static final String APP_NAME = "Java Sourcer";
}
