package org.eaSTars.javasourcer.gui.controller.impl;

import java.util.Locale;

import javax.swing.JPanel;

import org.eaSTars.javasourcer.gui.controller.DialogPanelController;
import org.springframework.context.MessageSource;

public abstract class AbstractDialogController extends AbstractInternationalizableController implements DialogPanelController {

	private JPanel panel;
	
	public AbstractDialogController(
			MessageSource messageSource,
			Locale locale) {
		super(messageSource, locale);
	}

	@Override
	public JPanel getPanel() {
		if (panel == null) {
			panel = buildPanel();
		}
		
		return panel;
	}

	protected abstract JPanel buildPanel();
	
}
