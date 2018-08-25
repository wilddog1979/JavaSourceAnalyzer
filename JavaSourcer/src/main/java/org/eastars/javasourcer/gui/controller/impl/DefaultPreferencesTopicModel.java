package org.eastars.javasourcer.gui.controller.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;

public class DefaultPreferencesTopicModel extends DefaultListModel<String> {

	private static final long serialVersionUID = -6606767058460605368L;

	private List<String> keys;
	
	public DefaultPreferencesTopicModel(String[][] values) {
		keys = Arrays.asList(values).stream().map(e -> {
			this.addElement(e[1]);
			return e[0];
		}).collect(Collectors.toList());
	}
	
	public String getKeyAt(int index) {
		return keys.get(index);
	}
	
}
