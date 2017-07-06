package org.eaSTars;

import java.awt.event.WindowEvent;

import org.eaSTars.adashboard.gui.impl.DefaultMainFrame;
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
		
		Application macApplication = Application.getApplication();
		macApplication.setAboutHandler(paramAboutEvent -> {});
		macApplication.setPreferencesHandler(paramPreferencesEvent -> {});
		
		macApplication.setQuitHandler((quitevent, quitresponse) -> {
			DefaultMainFrame mainframe = context.getBean(DefaultMainFrame.class);
			mainframe.dispatchEvent(new WindowEvent(mainframe, WindowEvent.WINDOW_CLOSING));
			context.close();
			quitresponse.performQuit();
		});
	}

}
