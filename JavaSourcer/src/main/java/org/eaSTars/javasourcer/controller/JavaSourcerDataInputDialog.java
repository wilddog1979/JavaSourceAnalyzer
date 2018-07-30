package org.eaSTars.javasourcer.controller;

import java.awt.Component;
import java.util.Optional;

public interface JavaSourcerDataInputDialog<T> {

	public Optional<T> getInputData(Component parent);
	
}
