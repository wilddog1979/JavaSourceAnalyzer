package org.eastars.javasourcer.gui.context;


import java.awt.Desktop;
import java.awt.Taskbar;

import org.eastars.javasourcer.gui.controller.MainFrameController;
import org.springframework.context.ConfigurableApplicationContext;

public class MacApplicationFunctions implements ApplicationFunctions {
	
	@Override
	public void initApplication(ConfigurableApplicationContext context, MainFrameController controller) {
		System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty(
				"com.apple.mrj.application.apple.menu.about.name",
				ApplicationResources.APP_NAME);
		
		Taskbar taskbar = Taskbar.getTaskbar();
		taskbar.setIconImage(ApplicationResources.APPICON.getImage());
		
		Desktop desktop = Desktop.getDesktop();
		desktop.setAboutHandler(paramAboutEvent -> controller.showAbout());
		desktop.setPreferencesHandler(paramPreferencesEvent -> controller.showPreferences());
		desktop.setQuitHandler((quitevent, quitresponse) -> {
			controller.dispatchClosingEvent();
			context.close();
			quitresponse.performQuit();
		});
	}

}
