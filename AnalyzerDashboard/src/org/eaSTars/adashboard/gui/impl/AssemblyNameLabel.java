package org.eaSTars.adashboard.gui.impl;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import org.eaSTars.adashboard.gui.MainFrameDelegate;

public class AssemblyNameLabel extends JLabel implements MouseListener {

	private static final long serialVersionUID = -4627552270369629213L;

	private MainFrameDelegate delegate;
	
	private Integer id;
	
	public AssemblyNameLabel(MainFrameDelegate delegate) {
		this.delegate = delegate;
		addMouseListener(this);
	}
	
	public void setAssemblyLink(String name, Integer id) {
		setText(name);
		this.id = id;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && delegate != null) {
			if (id != null) {
				delegate.openAssembly(id);
			} else {
				System.out.println("No ID!!!");
			}
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
		if (id != null) {
			this.setForeground(Color.BLUE);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (id != null) {
			this.setForeground(Color.BLACK);
		}
	}

}
