package org.eaSTars.adashboard.gui.dto;

import java.awt.Component;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JavaAssemblyDefinitionView extends JPanel {

	private static final long serialVersionUID = -4392057563975666383L;
	
	private JLabel typename = new JLabel();
	
	private JLabel name = new JLabel();
	
	private JPanel typeParameters = new JPanel();
	
	private JPanel typeExtends = new JPanel();
	
	private JPanel typeImplements = new JPanel();
	
	public JavaAssemblyDefinitionView() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(typename);
		add(new JLabel(" "));
		add(name);
		typeParameters.setLayout(new BoxLayout(typeParameters, BoxLayout.X_AXIS));
		add(typeParameters);
		typeExtends.setLayout(new BoxLayout(typeExtends, BoxLayout.X_AXIS));
		add(typeExtends);
		typeImplements.setLayout(new BoxLayout(typeImplements, BoxLayout.X_AXIS));
		add(typeImplements);
		add(Box.createHorizontalGlue());
	}
	
	public void setTypeName(String typename) {
		this.typename.setText(typename);
	}
	
	public void setName(String name) {
		this.name.setText(name);
	}
	
	public void setTypeParameters(List<Component> typeparameters) {
		if (!typeparameters.isEmpty()) {
			this.typeParameters.add(new JLabel("<"));
			for (int i = 0; i < typeparameters.size(); ++i) {
				if (i != 0) {
					this.typeParameters.add(new JLabel(", "));
				}
				this.typeParameters.add(typeparameters.get(i));
			}
			this.typeParameters.add(new JLabel(">"));
		}
	}
	
	public void setExtends(Component extendsParameter) {
		if (extendsParameter != null) {
			this.typeExtends.add(new JLabel(" extends "));
			this.typeExtends.add(extendsParameter);
		}
	}
	
	public void setImplements(List<Component> typeimplements) {
		if (!typeimplements.isEmpty()) {
			this.typeImplements.add(new JLabel(" implements "));
			for (int i = 0; i < typeimplements.size(); ++i) {
				if (i != 0) {
					this.typeImplements.add(new JLabel(", "));
				}
				this.typeImplements.add(typeimplements.get(i));
			}
		}
	}
}
