package org.eaSTars;

import org.eaSTars.adashboard.controller.ADashboardController;
import org.springframework.context.ConfigurableApplicationContext;

public class WindowsApplicationFunctions implements ApplicationFunctions {

	@Override
	public void initApplication(ConfigurableApplicationContext context) {
		ADashboardController controller = context.getBean(ADashboardController.class);
		controller.buildMenu(true);
	}

}
