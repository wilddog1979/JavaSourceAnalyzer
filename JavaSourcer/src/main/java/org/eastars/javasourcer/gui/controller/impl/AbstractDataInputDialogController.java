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

public abstract class AbstractDataInputDialogController<T, K> extends AbstractDialogController implements JavaSourcerDataInputDialog<T, K> {

	private boolean newobject = false;
	
	protected abstract JPanel buildPanel();
	
	protected abstract Optional<T> getDTOByKey(K key);
	
	protected abstract void cleanupPanel();
	
	protected abstract void initializePanel(T parameter);
	
	protected abstract void saveData(Optional<T> oldDTO, T newDto);
	
	protected abstract T getInputData();
	
	protected abstract boolean validateInputData();
	
	protected String getTitle() {
		return getResourceBundle(newobject ? ResourceBundle.TITLE_NEW : ResourceBundle.TITLE_PROPERTIES);
	}
	
	public AbstractDataInputDialogController(MessageSource messageSource, Locale locale) {
		super(messageSource, locale);
	}
	
	@Override
	public Optional<T> getInputData(Frame parent, K key, boolean save) {
		cleanupPanel();
		Optional<T> oldDTO = getDTOByKey(key); 
		oldDTO.ifPresent(this::initializePanel);
		
		T inputdata = null;
		while (showDialog(parent)) {
			if (validateInputData()) {
				inputdata = getInputData();
				if (save) {
					saveData(oldDTO, inputdata);
				}
				break;
			}
		}
		return Optional.ofNullable(inputdata);
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
