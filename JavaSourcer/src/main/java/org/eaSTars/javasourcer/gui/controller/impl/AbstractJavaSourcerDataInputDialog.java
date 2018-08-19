package org.eaSTars.javasourcer.gui.controller.impl;

import java.awt.Color;
import java.awt.Component;
import java.util.Locale;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.eaSTars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eaSTars.javasourcer.gui.controller.JavaSourcerDataInputDialog;
import org.springframework.context.MessageSource;

public abstract class AbstractJavaSourcerDataInputDialog<T> extends AbstractInternationalizableController implements JavaSourcerDataInputDialog<T> {

	private JPanel settingspanel;
	
	protected abstract JPanel buildPanel();
	
	protected abstract void cleanupPanel();
	
	protected abstract void initializePanel(T parameter);
	
	protected abstract T getInputData();
	
	protected abstract boolean validateInputData();
	
	private boolean newobject = false;
	
	protected String getTitle() {
		return getResourceBundle(newobject ? ResourceBundle.TITLE_NEW : ResourceBundle.TITLE_PROPERTIES);
	}
	
	public AbstractJavaSourcerDataInputDialog(MessageSource messageSource, Locale locale) {
		super(messageSource, locale);
	}
	
	private JPanel getDialogPanel(T parameter, boolean error) {
		if (settingspanel == null) {
			settingspanel = buildPanel();
		}
		if (!error) {
			newobject = parameter == null;
			if (newobject) {
				cleanupPanel();
			} else {
				initializePanel(parameter);
			}
		}
		return settingspanel;
	}
	
	@Override
	public Optional<T> getInputData(Component parent, T input, boolean error) {
		return getInputDataInternal(parent, getDialogPanel(input, error));
	}
	
	@Override
	public Optional<T> getInputData(Component parent, boolean error) {
		return getInputDataInternal(parent, getDialogPanel(null, error));
	}
	
	protected boolean indicateError(JComponent component, boolean errorcondition) {
		if (errorcondition) {
			component.setBorder(BorderFactory.createLineBorder(Color.RED));
		} else {
			component.setBorder(null);
		}
		return errorcondition;
	}
	
	private Optional<T> getInputDataInternal(Component parent, JPanel panel) {
		//resetValidation();
		
		while (JOptionPane.showConfirmDialog(null, panel, getTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
			if (validateInputData()) {
				return Optional.of(getInputData());
			}
		}
		return Optional.empty();
	}
	
}
