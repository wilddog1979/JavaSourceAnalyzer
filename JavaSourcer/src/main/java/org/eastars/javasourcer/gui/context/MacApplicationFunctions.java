package org.eastars.javasourcer.gui.context;


import org.eastars.javasourcer.gui.controller.MainFrameController;
import org.springframework.context.ConfigurableApplicationContext;

import com.apple.eawt.Application;

public class MacApplicationFunctions implements ApplicationFunctions {
	
	@Override
	public void initApplication(ConfigurableApplicationContext context, MainFrameController controller) {
		System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty(
				"com.apple.mrj.application.apple.menu.about.name",
				ApplicationResources.APP_NAME);
		
		Application macApplication = Application.getApplication();
		macApplication.setDockIconImage(ApplicationResources.APPICON.getImage());
		macApplication.setAboutHandler(paramAboutEvent -> controller.showAbout());
		macApplication.setPreferencesHandler(paramPreferencesEvent -> controller.showPreferences());
		
		macApplication.setQuitHandler((quitevent, quitresponse) -> {
			controller.dispatchClosingEvent();
			context.close();
			quitresponse.performQuit();
		});
	}

}
