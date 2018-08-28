package org.eastars.javasourcer.gui.controller.impl;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.eastars.javasourcer.gui.context.ApplicationResources;
import org.springframework.context.MessageSource;

public abstract class AbstractDialogPanelController extends AbstractInternationalizableController {
	
	private JPanel panel;
	
	public AbstractDialogPanelController(
			MessageSource messageSource,
			Locale locale) {
		super(messageSource, locale);
	}

	public JPanel getPanel() {
		if (panel == null) {
			panel = buildPanel();
		}
		
		return panel;
	}
	
	protected abstract JPanel buildPanel();
	
	protected JPanel buildTableWithButtons(JTable table, int width, int height, ActionListener addAction, ActionListener removeAction) {
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPaneLibraries = new JScrollPane(table);
		scrollPaneLibraries.setPreferredSize(new Dimension(width, height));
		
		JPanel panelButton = new JPanel();
		JButton buttonAdd = new JButton(ApplicationResources.PLUSSIGN);
		buttonAdd.addActionListener(addAction);
		panelButton.add(buttonAdd);
		
		JButton buttonRemove = new JButton(ApplicationResources.MINUSSIGN);
		buttonRemove.addActionListener(removeAction);
		panelButton.add(buttonRemove);
		
		return makeCompactGrid(Arrays.asList(
				scrollPaneLibraries,
				panelButton),
				2, 1, 0, 0, 0, 0);
	}
	
	protected JPanel buildTableWithButtons(
			JTable table,
			int width,
			int height,
			ActionListener addAction,
			ActionListener removeAction,
			ActionListener editAction) {
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPaneLibraries = new JScrollPane(table);
		scrollPaneLibraries.setPreferredSize(new Dimension(width, height));
		
		JPanel panelButton = new JPanel();
		JButton buttonEdit = new JButton(ApplicationResources.THREEDOTS);
		buttonEdit.addActionListener(editAction);
		panelButton.add(buttonEdit);
		
		JButton buttonAdd = new JButton(ApplicationResources.PLUSSIGN);
		buttonAdd.addActionListener(addAction);
		panelButton.add(buttonAdd);
		
		JButton buttonRemove = new JButton(ApplicationResources.MINUSSIGN);
		buttonRemove.addActionListener(removeAction);
		panelButton.add(buttonRemove);
		
		return makeCompactGrid(Arrays.asList(
				scrollPaneLibraries,
				panelButton),
				2, 1, 0, 0, 0, 0);
	}
	
}
