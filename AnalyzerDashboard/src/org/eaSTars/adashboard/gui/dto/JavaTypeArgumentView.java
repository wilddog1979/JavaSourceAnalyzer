package org.eaSTars.adashboard.gui.dto;

import java.awt.Component;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.eaSTars.adashboard.gui.MainFrameDelegate;
import org.eaSTars.adashboard.gui.impl.AssemblyNameLabel;

public class JavaTypeArgumentView extends JPanel {

	private static final long serialVersionUID = -4185159758585742564L;
	
	private AssemblyNameLabel name;
	
	private JPanel arguments = new JPanel();
	
	public JavaTypeArgumentView(MainFrameDelegate delegate) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		name = new AssemblyNameLabel(delegate);
		add(name);
		arguments.setLayout(new BoxLayout(arguments, BoxLayout.X_AXIS));
		add(arguments);
	}
	
	public void setAssemblyLink(String name, Integer id) {
		this.name.setAssemblyLink(name, id);
	}
	
	public void setArguments(List<? extends Component> arguments) {
		if (!arguments.isEmpty()) {
			this.arguments.add(new JLabel("<"));
			for (int i = 0; i < arguments.size(); ++i) {
				if (i != 0) {
					this.arguments.add(new JLabel(", "));
				}
				this.arguments.add(arguments.get(i));
			}
			this.arguments.add(new JLabel(">"));
		}
	}
}
