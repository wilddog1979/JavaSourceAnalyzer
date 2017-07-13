package org.eaSTars.adashboard.gui.dto;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JavaSequenceDiagramView extends JPanel {

	private static final long serialVersionUID = 7211563422717320804L;

	private JLabel content = new JLabel();
	
	private Dimension dimension;
	
	private Image image;
	
	public JavaSequenceDiagramView() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		add(content);
		add(Box.createVerticalGlue());
	}
	
	public void setContentImage(Image image) {
		this.image = image;
		content.setIcon(new ImageIcon(image));
		dimension = content.getPreferredSize();
	}
	
	public void scaleImage(int scale) {
		int width = dimension.width * scale / 100;
		int height = dimension.height * scale / 100;
		content.setIcon(new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
	}
}
