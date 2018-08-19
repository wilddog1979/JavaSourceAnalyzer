package org.eaSTars.javasourcer.gui.controller.impl;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import javax.swing.AbstractAction;

public class CallbackAction extends AbstractAction {

	private static final long serialVersionUID = 6478101480145155751L;

	private Consumer<ActionEvent> action;
	
	public CallbackAction(Consumer<ActionEvent> action) {
		this.action = action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		action.accept(e);
	}

}
