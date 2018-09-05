package org.eastars.javasourcer.gui.context;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ApplicationResources {

	private ApplicationResources() {
	}
	
	public static class ResourceBundle {
		
		private ResourceBundle() {
		}
		
		public static final String GLOBAL_BUTTON_OK = "button.ok";
		
		public static final String GLOBAL_BUTTON_CANCEL = "button.cancel";
		
		public static final String MAIN_MENU_SWITCH = "menu.switch";
		
		public static final String MAIN_MENU_ADD = "menu.add";
		
		public static final String MAIN_MENU_PROJECT = "menu.project";
		
		public static final String MAIN_MENU_PROPERTIES = "menu.properties";
		
		public static final String MAIN_MENU_ANALYZE = "menu.analyze";
		
		public static final String MAIN_MENU_SETTINGS = "menu.settings";
		
		public static final String MAIN_MENU_PREFERENCES = "menu.preferences";
		
		public static final String MAIN_MENU_HELP = "menu.help";
		
		public static final String MAIN_MENU_ABOUT = "menu.about";
		
		public static final String TITLE = "title";
		
		public static final String TITLE_NEW = "title.new";
		
		public static final String TITLE_PROPERTIES = "title.properties";
		
		public static final String ROJECTDIALOG_NAME = "dialog.name";
		
		public static final String PROJECTDIALOG_DIRECTORY = "dialog.directory.label";
		
		public static final String PROJECTDIALOG_SELECT = "dialog.directory.select";
		
		public static final String PROJECTDIALOG_SOURCES = "dialog.directory.sources";
		
		public static final String PROJECTDIALOG_LIBRARIES = "dialog.directory.libraries";
		
		public static final String ABOUT_TEXT = "text";

		public static final String TOPIC_LIBRARIES = "topic.library";
		
		public static final String LABEL_LIBRARIES = "label.libraries";
		
		public static final String LABEL_PACKAGES = "label.packages";
		
		public static final String LABEL_MODULES = "label.modules";
		
		public static final String LABEL_SOURCEFOLDERS = "label.sourcefolders";
		
		public static final String NEW_ENTRY = "new.entry";
		
	}
	
	public static class Config {
		
		private Config() {
		}
		
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
	
	public static final ImageIcon THREEDOTS = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/threedots.png"));
	
	public static final ImageIcon PLUSSIGN = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/plussign.png"));
	
	public static final ImageIcon MINUSSIGN = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/minussign.png"));
	
	public static final ImageIcon ARROWLEFT = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/arrowleft.png"));
	
	public static final ImageIcon ARROWRIGHT = new ImageIcon(ApplicationResources.class.getClassLoader().getResource("icons/arrowright.png"));
	
	public static final String APP_NAME = "Java Sourcer";
}
