package org.eaSTars.adashboard.gui.dto;

import java.awt.Component;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eaSTars.adashboard.gui.MainFrameDelegate;
import org.eaSTars.adashboard.gui.impl.AssemblyNameLabel;

public class JavaTypeParameterView extends JPanel {

	private static final long serialVersionUID = 7384753055453317341L;
	
	private AssemblyNameLabel name;
	
	private JPanel bounds = new JPanel(); 
	
	public JavaTypeParameterView(MainFrameDelegate delegate) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		name = new AssemblyNameLabel(delegate);
		add(name);
		bounds.setLayout(new BoxLayout(bounds, BoxLayout.X_AXIS));
		add(bounds);
	}
	
	public void setAssemblyLink(String name, Integer id) {
		this.name.setAssemblyLink(name, id);
	}
	
	public void addBounds(List<? extends Component> bounds) {
		if (!bounds.isEmpty()) {
			this.bounds.add(new JLabel(" extends "));
			for (int i = 0; i < bounds.size(); ++i) {
				if (i != 0) {
					this.bounds.add(new JLabel(", "));
				}
				this.bounds.add(bounds.get(i));
			}
		}
	}
}
