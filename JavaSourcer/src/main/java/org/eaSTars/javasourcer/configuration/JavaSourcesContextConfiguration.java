package org.eaSTars.javasourcer.configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.eaSTars.javasourcer.controller.impl.DefaultMainFrameController;
import org.eaSTars.javasourcer.facade.ApplicationGuiFacade;
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

	@Bean
	public DefaultMainFrameController getDefaultMainFrameController(
			ConfigurableApplicationContext context,
			MessageSource messageSource,
			ApplicationGuiFacade applicationGuiFacade) {
		DefaultMainFrameController controller = new DefaultMainFrameController(
				messageSource,
				applicationGuiFacade);

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
	
	@Bean
	public Locale getLocale(ApplicationGuiFacade applicationGuiFacade) {
		return applicationGuiFacade.getLocale();
	}
	
}
