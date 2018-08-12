package org.eaSTars.javasourcer.gui.context;

import org.eaSTars.javasourcer.gui.controller.MainFrameController;
import org.springframework.context.ConfigurableApplicationContext;

public class WindowsApplicationFunctions implements ApplicationFunctions {

	@Override
	public void initApplication(ConfigurableApplicationContext context, MainFrameController controller) {
		controller.setExtendedMenu(true);
	}

}
