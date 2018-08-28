package org.eastars.javasourcer.gui.controller.impl;

import java.awt.Color;
import java.awt.Frame;
import java.util.Locale;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eastars.javasourcer.gui.controller.JavaSourcerDataInputDialog;
import org.springframework.context.MessageSource;

public abstract class AbstractDataInputDialogController<T> extends AbstractDialogController implements JavaSourcerDataInputDialog<T> {

	private boolean newobject = false;
	
	protected abstract JPanel buildPanel();
	
	protected abstract void cleanupPanel();
	
	protected abstract void initializePanel(T parameter);
	
	protected abstract T getInputData();
	
	protected abstract boolean validateInputData();
	
	protected String getTitle() {
		return getResourceBundle(newobject ? ResourceBundle.TITLE_NEW : ResourceBundle.TITLE_PROPERTIES);
	}
	
	public AbstractDataInputDialogController(MessageSource messageSource, Locale locale) {
		super(messageSource, locale);
	}
	
	@Override
	public Optional<T> getInputData(Frame parent, Optional<T> input, boolean error) {
		if (!error) {
			newobject = !input.isPresent();
			if (newobject) {
				cleanupPanel();
			} else {
				initializePanel(input.get());
			}
		}
		
		while (showDialog(parent)) {
			if (validateInputData()) {
				return Optional.of(getInputData());
			}
		}
		return Optional.empty();
	}
	
	protected boolean indicateError(JComponent component, boolean errorcondition) {
		if (errorcondition) {
			component.setBorder(BorderFactory.createLineBorder(Color.RED));
		} else {
			component.setBorder(null);
		}
		return errorcondition;
	}
	
}
