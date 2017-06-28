package org.eaSTars.adashboard.gui.dto;

import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JavaSequenceDiagramView extends JPanel {

	private static final long serialVersionUID = 7211563422717320804L;

	private JLabel content = new JLabel();
	
	public JavaSequenceDiagramView() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		add(content);
		add(Box.createVerticalGlue());
	}
	
	public void setContentImage(Image image) {
		content.setIcon(new ImageIcon(image));
	}
}
