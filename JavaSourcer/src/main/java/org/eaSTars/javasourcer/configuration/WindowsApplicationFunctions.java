package org.eaSTars.javasourcer.configuration;

import org.eaSTars.javasourcer.controller.MainFrameController;
import org.springframework.context.ConfigurableApplicationContext;

public class WindowsApplicationFunctions implements ApplicationFunctions {

	@Override
	public void initApplication(ConfigurableApplicationContext context, MainFrameController controller) {
		controller.setExtendedMenu(true);
	}

}
