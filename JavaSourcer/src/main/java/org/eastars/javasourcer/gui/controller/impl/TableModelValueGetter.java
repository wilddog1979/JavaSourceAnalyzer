package org.eastars.javasourcer.gui.controller.impl;

import java.io.Serializable;

@FunctionalInterface
public interface TableModelValueGetter<T extends Serializable> extends Serializable {

	public Object getValue(T entry, int columnIndex);
	
}
