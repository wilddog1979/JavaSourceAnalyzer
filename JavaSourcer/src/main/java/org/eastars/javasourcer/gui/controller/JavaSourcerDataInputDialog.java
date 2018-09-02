package org.eastars.javasourcer.gui.controller;

import java.awt.Frame;
import java.util.Optional;

public interface JavaSourcerDataInputDialog<T, K> {

	public Optional<T> getInputData(Frame parent, K key, boolean save);
	
}
