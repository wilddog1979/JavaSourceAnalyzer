package org.eaSTars.adashboard.gui.dto;

import java.awt.Component;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JavaMethodDefinitionView extends JPanel {

	private static final long serialVersionUID = 7792048835146623363L;

	private JPanel typeParameters = new JPanel();
	
	private JPanel type = new JPanel();
	
	private JLabel name = new JLabel();
	
	private JPanel parameters = new JPanel();
	
	public JavaMethodDefinitionView() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		typeParameters.setLayout(new BoxLayout(typeParameters, BoxLayout.X_AXIS));
		add(typeParameters);
		type.setLayout(new BoxLayout(type, BoxLayout.X_AXIS));
		add(type);
		add(new JLabel(" "));
		add(name);
		add(new JLabel("("));
		parameters.setLayout(new BoxLayout(parameters, BoxLayout.X_AXIS));
		add(parameters);
		add(new JLabel(")"));
		
		add(Box.createHorizontalGlue());
	}
	
	public void addTypeParameters(List<Component> typeparameters) {
		if (!typeparameters.isEmpty()) {
			this.typeParameters.add(new JLabel("<"));
			for (int i = 0; i < typeparameters.size(); ++i) {
				if (i != 0) {
					this.typeParameters.add(new JLabel(", "));
				}
				this.typeParameters.add(typeparameters.get(i));
			}
			this.typeParameters.add(new JLabel("> "));
		}
	}
	
	public void setType(Component component) {
		this.type.add(component);
	}
	
	public void setName(String name) {
		this.name.setText(name);
	}
	
	public void addParameters(List<Component> parameters) {
		for (int i = 0; i < parameters.size(); ++i) {
			if (i != 0) {
				this.parameters.add(new JLabel(", "));
			}
			this.parameters.add(parameters.get(i));
		}
	}
}
