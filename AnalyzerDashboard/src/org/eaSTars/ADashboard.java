package org.eaSTars;

import java.awt.event.WindowEvent;

import org.eaSTars.adashboard.gui.MainFrame;
import org.eaSTars.adashboard.gui.MainFrameAdapter;
import org.eaSTars.adashboard.gui.impl.DefaultMainFrame;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.apple.eawt.Application;

public class ADashboard {

	private static final String APP_NAME = "ADashboard";
	
	private ConfigurableApplicationContext context = new FileSystemXmlApplicationContext("resources/AnalyzerDashboardContext.xml");
	
	public static void main(String[] args) {
		new ADashboard();
	}

	public ADashboard() {
		initApplication();
		
		context.getBean(MainFrameAdapter.class).addFrameClosedListener(w -> context.close());
		context.getBean(MainFrame.class).start();
	}
	
	private void initApplication() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("mac") != -1) {
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
}
