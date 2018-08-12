package org.eaSTars.javasourcer.gui.controller.impl;

import static org.eaSTars.javasourcer.gui.context.ApplicationResources.ResourceBundle.ABOUT_TEXT;
import static org.eaSTars.javasourcer.gui.context.ApplicationResources.ResourceBundle.TITLE_NEW;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.eaSTars.javasourcer.gui.context.ApplicationResources;
import org.eaSTars.javasourcer.gui.controller.JavaSourcerDialog;
import org.eaSTars.javasourcer.gui.service.ApplicationGuiService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("aboutdailogcontroller")
public class DefaultJavaSourcerAboutDialogController extends AbstractInternationalizableController implements JavaSourcerDialog {

	public DefaultJavaSourcerAboutDialogController(
			MessageSource messageSource,
			ApplicationGuiService applicationGuiService) {
		super(messageSource, applicationGuiService.getLocale());
	}

	@Override
	public boolean showDialog(java.awt.Component parent) {
		JLabel label = new JLabel(getResourceBundle(ABOUT_TEXT), new ImageIcon(ApplicationResources.APPICON16), JLabel.LEFT);
		JOptionPane.showMessageDialog(parent, label, getResourceBundle(TITLE_NEW), JOptionPane.PLAIN_MESSAGE);
		return true;
	}

}
