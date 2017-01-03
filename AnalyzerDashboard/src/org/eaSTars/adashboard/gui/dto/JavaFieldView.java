package org.eaSTars.adashboard.gui.dto;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JavaFieldView extends JPanel {

	private static final long serialVersionUID = 6549896914981051030L;

	private JPanel type = new JPanel();
	
	private JLabel name = new JLabel();
	
	public JavaFieldView() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		type.setLayout(new BoxLayout(type, BoxLayout.X_AXIS));
		add(type);
		add(new JLabel(" "));
		add(name);
		
		add(Box.createHorizontalGlue());
	}
	
	public void setName(String name) {
		this.name.setText(name);
	}
	
	public void setType(Component component) {
		this.type.add(component);
	}
}
