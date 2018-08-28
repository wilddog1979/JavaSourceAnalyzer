package org.eastars.javasourcer.gui.controller.impl;

import java.io.Serializable;

@FunctionalInterface
public interface TableModelValueSetter<T extends Serializable> extends Serializable {

	public T setValue(T entry, int columnIndex, Object aValue);
	
}
