package org.eaSTars.adashboard.gui.dto;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class JavaAssemblyFullView extends JPanel {

	private static final long serialVersionUID = -105260036669043124L;
	
	private JPanel content = new JPanel();
	
	public JavaAssemblyFullView() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		add(content);
		add(Box.createVerticalGlue());
	}
	
	public void addEntry(Component component) {
		content.add(component);
	}
}
