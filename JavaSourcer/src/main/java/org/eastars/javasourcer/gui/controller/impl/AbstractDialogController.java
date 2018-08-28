package org.eastars.javasourcer.gui.controller.impl;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eastars.javasourcer.gui.controller.JavaSourcerDialog;
import org.springframework.context.MessageSource;

public abstract class AbstractDialogController extends AbstractDialogPanelController implements JavaSourcerDialog {

	private JDialog dialog;
	
	private boolean result;
	
	public AbstractDialogController(MessageSource messageSource, Locale locale) {
		super(messageSource, locale);
	}

	protected abstract String getTitle();

	@Override
	public boolean showDialog(Frame parent) {
		if (dialog == null) {
			buildDialog(parent);
		}
		
		Dimension dialogsize = dialog.getSize();
		Rectangle parentbounds = parent.getBounds();
		dialog.setLocation(
				Math.max(parentbounds.x + parentbounds.getSize().width / 2 - dialogsize.width / 2, 0),
				Math.max(parentbounds.y + parentbounds.getSize().height / 2 - dialogsize.height / 2, 0));
		
		dialog.setVisible(true);
		
		return result;
	}

	private void buildDialog(Frame parent) {
		dialog = new JDialog(parent, getTitle(), true);
		
		dialog.add(makeCompactGrid(Arrays.asList(
				buildPanel(),
				buildButtonPanel()),
				2, 1, 0, 0, 0, 0));
		
		dialog.pack();
		dialog.setResizable(false);
	}
	
	protected JPanel buildButtonPanel() {
		JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton buttonOK = new JButton(getGlobalResourceBundle(ResourceBundle.GLOBAL_BUTTON_OK));
		buttonOK.addActionListener(a -> dialogAction(true));
		panelBottomButtons.add(buttonOK);
		
		JButton buttonCancel = new JButton(getGlobalResourceBundle(ResourceBundle.GLOBAL_BUTTON_CANCEL));
		buttonCancel.addActionListener(a -> dialogAction(false));
		panelBottomButtons.add(buttonCancel);
		
		registerHotKeyAction(dialog.getRootPane(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "dialogyes", e -> dialogAction(true));
		registerHotKeyAction(dialog.getRootPane(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "dialogno", e -> dialogAction(false));
		
		return panelBottomButtons;
	}
	
	protected void dialogAction(boolean result) {
		dialog.dispose();
		this.result = result;
	}

}
