package org.eaSTars.javasourcer.controller.impl;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.eaSTars.javasourcer.controller.JavaSourcerMessageDialog;

@org.springframework.stereotype.Component("messagedialog")
public class DefaultJavaSourcerMessageDialog implements JavaSourcerMessageDialog {

	@Override
	public boolean showMessage(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
		return true;
	}

}
