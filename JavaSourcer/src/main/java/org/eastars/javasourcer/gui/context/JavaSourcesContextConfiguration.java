package org.eastars.javasourcer.gui.context;

import java.awt.Desktop;
import java.awt.Taskbar;
import java.awt.Desktop.Action;
import java.awt.Taskbar.Feature;
import java.util.HashSet;
import java.util.List;

import org.eastars.javasourcer.analyzis.facade.ProjectAnalysisFacade;
import org.eastars.javasourcer.gui.controller.ApplicationController;
import org.eastars.javasourcer.gui.controller.DialogControllers;
import org.eastars.javasourcer.gui.controller.impl.DefaultMainFrameController;
import org.eastars.javasourcer.gui.service.ApplicationGuiService;
import org.eastars.javasourcer.gui.service.ProjectMapperService;
import org.eastars.javasourcer.gui.service.ProjectService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class JavaSourcesContextConfiguration {

	private ConfigurableApplicationContext context;
	
	public JavaSourcesContextConfiguration(
			ConfigurableApplicationContext context) {
		this.context = context;
	}
	
	@Bean
	public ApplicationController getDefaultMainFrameController(
			MessageSource messageSource,
			ApplicationGuiService applicationGuiService,
			ProjectMapperService projectMappingService,
			ProjectService projectService,
			ProjectAnalysisFacade projectAnalysisFacade,
			DialogControllers dialogControllers) {
		ApplicationController controller = new DefaultMainFrameController(
				messageSource,
				applicationGuiService,
				projectMappingService,
				projectService,
				projectAnalysisFacade,
				dialogControllers);

		initializeApplication(controller);
		
		return controller;
	}
	
	private void initializeApplication(ApplicationController controller) {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("mac") != -1) {
			System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name",
					ApplicationResources.APP_NAME);
		} else {
			controller.setExtendedMenu(true);
		}
		
		if (Taskbar.isTaskbarSupported()) {
			Taskbar taskbar = Taskbar.getTaskbar();
			if (taskbar.isSupported(Feature.ICON_IMAGE)) {
				taskbar.setIconImage(ApplicationResources.APPICON.getImage());
			}
		}
		
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Action.APP_ABOUT)) {
				desktop.setAboutHandler(paramAboutEvent -> controller.showAbout());
			}
			if (desktop.isSupported(Action.APP_PREFERENCES)) {
				desktop.setPreferencesHandler(paramPreferencesEvent -> controller.showPreferences());
			}
			if (desktop.isSupported(Action.APP_QUIT_HANDLER)) {
				desktop.setQuitHandler((quitevent, quitresponse) -> {
					controller.dispatchClosingEvent();
					context.close();
					quitresponse.performQuit();
				});
			}
		}
	}
	
	@Bean(name="javasourcerconversionservice")
	public ConversionService getConversionServiceFactoryBean(List<Converter<?,?>> converters) {
	    ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
	    conversionServiceFactoryBean.setConverters(new HashSet<>(converters));
	    conversionServiceFactoryBean.afterPropertiesSet();
	    return conversionServiceFactoryBean.getObject();
	}
	
	@Bean
    public ReloadableResourceBundleMessageSource messageSource(){
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:locale/ResourceBundle");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
	
}
