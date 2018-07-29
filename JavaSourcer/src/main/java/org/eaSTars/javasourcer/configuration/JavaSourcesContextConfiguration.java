package org.eaSTars.javasourcer.configuration;

import java.util.Optional;

import org.eaSTars.javasourcer.controller.impl.DefaultMainFrameController;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JavaSourcesContextConfiguration {

	@Bean
	public DefaultMainFrameController getDefaultMainFrameController(ConfigurableApplicationContext context) {
		DefaultMainFrameController controller = new DefaultMainFrameController();

		instantiate(getApplicationFunctionClassName(), ApplicationFunctions.class)
		.ifPresent(i -> i.initApplication(context, controller));

		return controller;
	}
	
	private String getApplicationFunctionClassName() {
		String name = "org.eaSTars.javasourcer.configuration.WindowsApplicationFunctions";
		
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("mac") != -1) {
			name = "org.eaSTars.javasourcer.configuration.MacApplicationFunctions";
		}
		
		return name;
	}
	
	private <T> Optional<T> instantiate(String name, Class<T> type) {
		try {
			return Optional.ofNullable(type.cast(Class.forName(name).newInstance()));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			return Optional.empty();
		}
	}
}
