package org.eaSTars;

import java.awt.Desktop;
import java.awt.Taskbar;

import org.eaSTars.adashboard.controller.ADashboardController;
import org.eaSTars.adashboard.gui.resources.Resources;
import org.springframework.context.ConfigurableApplicationContext;

public class MacApplicationFunctions implements ApplicationFunctions {
	
	@Override
	public void initApplication(ConfigurableApplicationContext context) {
		System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty(
				"com.apple.mrj.application.apple.menu.about.name",
				Resources.APP_NAME);
		
		ADashboardController controller = context.getBean(ADashboardController.class);
		controller.buildMenu(false);
		
		Taskbar taskbar = Taskbar.getTaskbar();
		taskbar.setIconImage(Resources.APPICON.getImage());
		
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
