package org.eaSTars.adashboard.gui.dto;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class JavaMethodView extends JPanel {

	private static final long serialVersionUID = -5830679846561843784L;

	private JPanel content = new JPanel();
	
	public JavaMethodView() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		add(content);
	}
	
	public void addEntry(Component component) {
		content.add(component);
	}
}
