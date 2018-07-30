package org.eaSTars.javasourcer.controller.impl;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public abstract class AbstractFrameController extends JFrame {

	private static final long serialVersionUID = -3182407145498086393L;

	public JMenuItem createMenuItem(String name, ActionListener actionListener) {
		JMenuItem menuitem = new JMenuItem(name);
		
		if (actionListener != null) {
			menuitem.addActionListener(actionListener);
		}
		
		return menuitem;
	}
	
	public JMenuItem createMenuItem(String name, char shortcut, ActionListener actionListener) {
		JMenuItem menuitem = createMenuItem(name, actionListener);
		
		menuitem.setAccelerator(
				KeyStroke.getKeyStroke(
						shortcut,
						Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
						)
				);
		
		return menuitem;
	}
	
	public JMenuItem createMenuItem(String name, String shortcut, ActionListener actionListener) {
		JMenuItem menuitem = createMenuItem(name, actionListener);
		
		menuitem.setAccelerator(KeyStroke.getKeyStroke(shortcut));
		
		return menuitem;
	}
	
	public JMenu createMenu(String name, JMenuItem...items) {
		JMenu menu = new JMenu(name);
		
		Arrays.asList(items).forEach(item ->{
			if (item instanceof JMenuItemSeparator) {
				menu.addSeparator();
			} else {
				menu.add(item);
			}
		});
		
		return menu;
	}
	
}
