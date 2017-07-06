package org.eaSTars;

import java.util.Optional;

import org.eaSTars.adashboard.gui.MainFrame;
import org.eaSTars.adashboard.gui.MainFrameAdapter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ADashboard {

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
			instantiate("org.eaSTars.MacApplicationFunctions", ApplicationFunctions.class).ifPresent(i -> i.initApplication(context));
		}
	}
	
	private <T> Optional<T> instantiate(String name, Class<T> type) {
		try {
			return Optional.ofNullable(type.cast(Class.forName(name).newInstance()));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			return Optional.empty();
		}
	}
}
