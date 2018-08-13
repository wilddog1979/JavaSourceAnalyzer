package org.eaSTars.javasourcer.gui.context;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ApplicationResources {

	public static class ResourceBundle {
		
		public static final String MAIN_MENU_SWITCH = "menu.switch";
		
		public static final String MAIN_MENU_ADD = "menu.add";
		
		public static final String MAIN_MENU_PROJECT = "menu.project";
		
		public static final String MAIN_MENU_PROPERTIES = "menu.properties";
		
		public static final String MAIN_MENU_SETTINGS = "menu.settings";
		
		public static final String MAIN_MENU_PREFERENCES = "menu.preferences";
		
		public static final String MAIN_MENU_HELP = "menu.help";
		
		public static final String MAIN_MENU_ABOUT = "menu.about";
		
		public static final String TITLE = "title";
		
		public static final String TITLE_NEW = "title.new";
		
		public static final String TITLE_PROPERTIES = "title.properties";
		
		public static final String CREATEPROJECT_DIALOG_NAME = "dialog.name";
		
		public static final String CREATEPROJECT_DIALOG_DIRECTORY = "dialog.directory.label";
		
		public static final String CREATEPROJECT_DIALOG_SELECT = "dialog.directory.select";
		
		public static final String ABOUT_TEXT = "text";
		
	}
	
	public static class Config {
		
		public static final String GUI_X = "javasourcer.gui.x";
		
		public static final String GUI_Y = "javasourcer.gui.y";
		
		public static final String GUI_WIDTH = "javasourcer.gui.width";
		
		public static final String GUI_HEIGHT = "javasourcer.gui.height";
		
		public static final String GUI_DIVIDER = "javasourcer.gui.divider";
		
		public static final String GUI_LANGUAGE = "javasourcer.gui.language";

		public static final String DEFAULT_LANGUAGE = "en_US";
		
	}
	
	public static final ImageIcon APPICON = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/dashboard.png"));
	
	public static final Image APPICON16 = APPICON.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
	
	public static final ImageIcon PACKAGEICON = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/packageicon.png"));
	
	public static final ImageIcon CLASSICON = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/classicon.png"));
	
	public static final String APP_NAME = "Java Sourcer";
}
