package org.eaSTars.adashboard.gui.dto;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Optional;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.eaSTars.adashboard.controller.JavaSequenceDiagramDelegate;

public class JavaSequenceDiagramView extends JPanel implements MouseListener {

	private static final long serialVersionUID = 7211563422717320804L;

	private JMenuItem menuitemSaveScript = new JMenuItem("Save script...");
	
	private JMenuItem menuitemSaveImage = new JMenuItem("Save image...");
	
	private JLabel content = new JLabel();
	
	private Dimension dimension;
	
	private Image image;
	
	private JavaSequenceDiagramDelegate delegate;
	
	public JavaSequenceDiagramView() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.addMouseListener(this);
		add(content);
		add(Box.createVerticalGlue());
		
		menuitemSaveScript.addActionListener(a -> Optional.ofNullable(delegate).ifPresent(d -> d.saveScript()));
		menuitemSaveImage.addActionListener(a -> Optional.ofNullable(delegate).ifPresent(d -> d.saveImage()));
	}
	
	public void setContentImage(Image image) {
		this.image = image;
		content.setIcon(new ImageIcon(image));
		dimension = content.getPreferredSize();
	}
	
	public void scaleImage(int scale) {
		int width = dimension.width * scale / 100;
		int height = dimension.height * scale / 100;
		content.setIcon(new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_FAST)));
	}

	public JavaSequenceDiagramDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(JavaSequenceDiagramDelegate delegate) {
		this.delegate = delegate;
	}

	private void triggerPopup(MouseEvent e) {
		JPopupMenu popupmenu = new JPopupMenu();
		popupmenu.setLocation(e.getPoint());
		popupmenu.add(menuitemSaveScript);
		popupmenu.add(menuitemSaveImage);
		
		popupmenu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			triggerPopup(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
}
