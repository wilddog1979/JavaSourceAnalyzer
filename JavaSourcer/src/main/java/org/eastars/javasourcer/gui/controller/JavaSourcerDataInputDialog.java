package org.eastars.javasourcer.gui.controller;

import java.awt.Frame;
import java.util.Optional;

public interface JavaSourcerDataInputDialog<T> {

	public Optional<T> getInputData(Frame parent, Optional<T> input, boolean error);
	
}
