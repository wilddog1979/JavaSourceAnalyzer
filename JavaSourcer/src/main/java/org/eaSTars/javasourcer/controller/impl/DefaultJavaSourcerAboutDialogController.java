package org.eaSTars.javasourcer.controller.impl;

import static org.eaSTars.javasourcer.configuration.ApplicationResources.ResourceBundle.*;

import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.eaSTars.javasourcer.configuration.ApplicationResources;
import org.eaSTars.javasourcer.controller.JavaSourcerDialog;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("aboutdailogcontroller")
public class DefaultJavaSourcerAboutDialogController extends AbstractInternationalizableController implements JavaSourcerDialog {

	public DefaultJavaSourcerAboutDialogController(MessageSource messageSource, Locale locale) {
		super(messageSource, locale);
	}

	@Override
	public boolean showDialog(java.awt.Component parent) {
		JLabel label = new JLabel(getResourceBundle(ABOUT_TEXT), new ImageIcon(ApplicationResources.APPICON16), JLabel.LEFT);
		JOptionPane.showMessageDialog(parent, label, getResourceBundle(TITLE), JOptionPane.PLAIN_MESSAGE);
		return true;
	}

}
