package org.eaSTars.javasourcer.controller.impl;

import java.util.Locale;

import org.springframework.context.MessageSource;

public class AbstractInternationalizableController {

	private Locale locale;
	
	private MessageSource messageSource;
	
	public AbstractInternationalizableController(MessageSource messageSource, Locale locale) {
		this.messageSource = messageSource;
		this.locale = locale;
	}
	
	protected String getResourceBundle(String key) {
		return messageSource.getMessage(String.format("%s.%s", this.getClass().getSimpleName().toLowerCase(), key), null, locale);
	}
}
