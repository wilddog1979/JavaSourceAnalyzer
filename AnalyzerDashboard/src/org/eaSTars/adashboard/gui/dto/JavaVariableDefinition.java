package org.eaSTars.adashboard.gui.dto;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JavaVariableDefinition extends JPanel {

	private static final long serialVersionUID = -420621935878793276L;

	private JPanel type = new JPanel();
	
	private JLabel name = new JLabel();
	
	public JavaVariableDefinition() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		type.setLayout(new BoxLayout(type, BoxLayout.X_AXIS));
		add(type);
		add(new JLabel(" "));
		add(name);
	}

	public void setType(Component component) {
		this.type.add(component);
	}
	
	public void setName(String name) {
		this.name.setText(name);
	}

}
