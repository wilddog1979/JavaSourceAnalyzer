package org.eaSTars;

import org.eaSTars.binprocessor.service.BinaryScanner;
import org.eaSTars.sca.gui.impl.DefaultSourceCodeAnalizerGUI;
import org.eaSTars.sca.service.SCADatabaseMaintenanceService;
import org.eaSTars.util.ConfigService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class BinaryProcessor {
	private static final String APP_INITDATABASE = "app.conf.initdatabase";
	
	private ConfigurableApplicationContext context = new FileSystemXmlApplicationContext("resources/BinaryProcessorContext.xml");
	
	public static void main(String[] args) {
		new BinaryProcessor();
	}

	private BinaryProcessor() {
		try {
		ConfigService configService = context.getBean(ConfigService.class);
		
		if (configService.getProperty(APP_INITDATABASE, "false").equals("true")) {
			SCADatabaseMaintenanceService dblayer = context.getBean(SCADatabaseMaintenanceService.class);
			dblayer.init();
		}
		
		context.getBean(BinaryScanner.class).beginScanning();
		} finally {
			context.getBean(DefaultSourceCodeAnalizerGUI.class).completed();
		}
	}
}
