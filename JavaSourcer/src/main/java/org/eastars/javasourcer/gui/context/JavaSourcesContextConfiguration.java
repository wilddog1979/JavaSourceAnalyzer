package org.eastars.javasourcer.gui.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.eastars.javasourcer.gui.controller.JavaSourcerDataInputDialog;
import org.eastars.javasourcer.gui.controller.JavaSourcerDialog;
import org.eastars.javasourcer.gui.controller.impl.DefaultMainFrameController;
import org.eastars.javasourcer.gui.dto.ProjectDTO;
import org.eastars.javasourcer.gui.service.ApplicationGuiService;
import org.eastars.javasourcer.gui.service.ProjectMapperService;
import org.eastars.javasourcer.gui.service.ProjectService;
import org.springframework.beans.factory.annotation.Qualifier;
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
	public DefaultMainFrameController getDefaultMainFrameController(
			MessageSource messageSource,
			ApplicationGuiService applicationGuiService,
			ProjectMapperService projectMappingService,
			ProjectService projectService,
			@Qualifier("aboutdailogcontroller") JavaSourcerDialog aboutDialog,
			@Qualifier("preferencesdailogcontroller") JavaSourcerDialog preferencesDialog,
			@Qualifier("projectdailogcontroller") JavaSourcerDataInputDialog<ProjectDTO, String> projectDialog) {
		DefaultMainFrameController controller = new DefaultMainFrameController(
				messageSource,
				applicationGuiService,
				projectMappingService,
				projectService,
				aboutDialog,
				preferencesDialog,
				projectDialog);

		instantiate(getApplicationFunctionClassName(), ApplicationFunctions.class)
		.ifPresent(i -> i.initApplication(context, controller));

		return controller;
	}
	
	private String getApplicationFunctionClassName() {
		String name = "org.eastars.javasourcer.gui.context.WindowsApplicationFunctions";
		
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("mac") != -1) {
			name = "org.eastars.javasourcer.gui.context.MacApplicationFunctions";
		}
		
		return name;
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
