package org.eaSTars;

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
		context.getBean(MainFrameAdapter.class).addFrameClosedListener(w -> context.close());
		context.getBean(MainFrame.class).start();
	}
}
