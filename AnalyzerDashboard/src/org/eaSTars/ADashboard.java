package org.eaSTars;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.eaSTars.adashboard.controller.ADashboardController;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ADashboard {

	private ConfigurableApplicationContext context = new FileSystemXmlApplicationContext("resources/AnalyzerDashboardContext.xml");
	
	public static void main(String[] args) {
		new ADashboard();
	}

	public ADashboard() {
		initApplication();
		
		ADashboardController controller = context.getBean(ADashboardController.class);
		controller.addFrameClosedListener(w -> context.close());
		controller.start();
	}
	
	private void initApplication() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("mac") != -1) {
			instantiate("org.eaSTars.MacApplicationFunctions", ApplicationFunctions.class).ifPresent(i -> i.initApplication(context));
		} else {
			instantiate("org.eaSTars.WindowsApplicationFunctions", ApplicationFunctions.class).ifPresent(i -> i.initApplication(context));
		}
	}
	
	private <T> Optional<T> instantiate(String name, Class<T> type) {
		try {
			Class<?>[] constructorParameterTypes = {};
			Constructor<?> constructor = Class.forName(name).getConstructor(constructorParameterTypes);
			Object[] constructorParameters = {};
			return Optional.ofNullable(type.cast(constructor.newInstance(constructorParameters)));
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
			return Optional.empty();
		}
	}
}
