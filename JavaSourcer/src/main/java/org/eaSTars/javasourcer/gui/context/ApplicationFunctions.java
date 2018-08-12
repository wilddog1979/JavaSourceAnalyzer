package org.eaSTars.javasourcer.gui.context;

import org.eaSTars.javasourcer.gui.controller.MainFrameController;
import org.springframework.context.ConfigurableApplicationContext;

public interface ApplicationFunctions {

	public void initApplication(ConfigurableApplicationContext context, MainFrameController controller);
}
