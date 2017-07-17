package org.eaSTars;

import org.eaSTars.adashboard.controller.ADashboardController;
import org.springframework.context.ConfigurableApplicationContext;

import com.apple.eawt.Application;

public class MacApplicationFunctions implements ApplicationFunctions {

	private static final String APP_NAME = "ADashboard";
	
	@Override
	public void initApplication(ConfigurableApplicationContext context) {
		System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty(
				"com.apple.mrj.application.apple.menu.about.name",
				APP_NAME);
		
		ADashboardController controller = context.getBean(ADashboardController.class);
		controller.buildMenu(false);
		
		Application macApplication = Application.getApplication();
		macApplication.setAboutHandler(paramAboutEvent -> controller.showAbout());
		macApplication.setPreferencesHandler(paramPreferencesEvent -> controller.showPreferences());
		
		macApplication.setQuitHandler((quitevent, quitresponse) -> {
			controller.dispatchClosingEvent();
			context.close();
			quitresponse.performQuit();
		});
	}

}
