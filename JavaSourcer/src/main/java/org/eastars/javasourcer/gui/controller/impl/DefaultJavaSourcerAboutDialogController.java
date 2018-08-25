package org.eastars.javasourcer.gui.controller.impl;

import static org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle.ABOUT_TEXT;
import static org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle.TITLE;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.eastars.javasourcer.gui.context.ApplicationResources;
import org.eastars.javasourcer.gui.controller.JavaSourcerDialog;
import org.eastars.javasourcer.gui.service.ApplicationGuiService;
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
	public boolean showDialog(Frame parent) {
		JLabel label = new JLabel(getResourceBundle(ABOUT_TEXT), new ImageIcon(ApplicationResources.APPICON16), JLabel.LEFT);
		JOptionPane.showMessageDialog(parent, label, getResourceBundle(TITLE), JOptionPane.PLAIN_MESSAGE);
		return true;
	}

}
