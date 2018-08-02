package org.eaSTars.javasourcer.controller.impl;

import static org.eaSTars.javasourcer.configuration.ApplicationResources.ResourceBundle.*;

import java.awt.Component;
import java.util.Locale;

import javax.swing.JOptionPane;

import org.eaSTars.javasourcer.controller.JavaSourcerMessageDialog;
import org.springframework.context.MessageSource;

@org.springframework.stereotype.Component("messagedialog")
public class DefaultJavaSourcerMessageDialog extends AbstractInternationalizableController implements JavaSourcerMessageDialog {

	public DefaultJavaSourcerMessageDialog(MessageSource messageSource, Locale locale) {
		super(messageSource, locale);
	}

	@Override
	public boolean showMessage(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, getResourceBundle(TITLE), JOptionPane.ERROR_MESSAGE);
		return true;
	}

}
