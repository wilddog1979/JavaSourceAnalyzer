package org.eastars.javasourcer.gui.context;

import org.eastars.javasourcer.gui.controller.MainFrameController;
import org.springframework.context.ConfigurableApplicationContext;

public interface ApplicationFunctions {

	public void initApplication(ConfigurableApplicationContext context, MainFrameController controller);
}
