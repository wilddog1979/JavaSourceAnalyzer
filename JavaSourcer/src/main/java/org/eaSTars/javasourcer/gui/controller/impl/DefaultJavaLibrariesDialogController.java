package org.eaSTars.javasourcer.gui.controller.impl;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.eaSTars.javasourcer.data.service.JavaSourcerDataService;
import org.eaSTars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eaSTars.javasourcer.gui.service.ApplicationGuiService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("javalibrarydialogcontroller")
public class DefaultJavaLibrariesDialogController extends AbstractDialogController {

	private JavaSourcerDataService dataService;
	
	private DefaultTableModel libraryModel = new DefaultTableModel();
	
	private JTable libraries = new JTable(libraryModel);
	
	private DefaultTableModel packageModel = new DefaultTableModel();
	
	private JTable packages = new JTable(packageModel);
	
	public DefaultJavaLibrariesDialogController(
			MessageSource messageSource,
			ApplicationGuiService applicationGuiService,
			JavaSourcerDataService dataService) {
		super(messageSource, applicationGuiService.getLocale());
		this.dataService = dataService;
	}

	@Override
	protected JPanel buildPanel() {
		JPanel javaLibraryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		javaLibraryPanel.add(buildTableWithButton(libraries, libraryModel, 100, a -> {
			libraryModel.addRow(new Object[] {getResourceBundle(ResourceBundle.NEW_ENTRY)});
			int index = libraryModel.getRowCount();
			libraries.scrollRectToVisible(libraries.getCellRect(index - 1, 0, true));
			libraries.editCellAt(index - 1, 0);
		}));
		
		javaLibraryPanel.add(buildTableWithButton(packages, packageModel, 200, a -> {
			packageModel.addRow(new Object[] {getResourceBundle(ResourceBundle.NEW_ENTRY)});
			int index = packageModel.getRowCount();
			packages.scrollRectToVisible(packages.getCellRect(index - 1, 0, true));
			packages.editCellAt(index - 1, 0);
		}));
		
		libraries.getSelectionModel().addListSelectionListener(l -> {
			String libraryName = (String) libraryModel.getValueAt(libraries.getSelectedRow(), 0);
			initializePackage();
			dataService.getPackageNames(libraryName).forEach(p -> packageModel.addRow(new Object[] {p}));
		});
		
		return javaLibraryPanel;
	}

	private JPanel buildTableWithButton(JTable table, DefaultTableModel model, int width, ActionListener actionListener) {
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPaneLibraries = new JScrollPane(table);
		scrollPaneLibraries.setPreferredSize(new Dimension(width, 150));
		
		JButton buttonAddLibrary = new JButton(getResourceBundle(ResourceBundle.BUTTON_ADD));
		buttonAddLibrary.addActionListener(actionListener);
		
		JPanel panelButton = new JPanel();
		panelButton.add(buttonAddLibrary);
		
		return makeCompactGrid(Arrays.asList(
				scrollPaneLibraries,
				panelButton),
				2, 1, 0, 0, 0, 0);
	}
	
	@Override
	public void initializeContent() {
		libraryModel = new DefaultTableModel();
		libraryModel.setColumnIdentifiers(new Object[] {
				getResourceBundle(ResourceBundle.LABEL_LIBRARIES)
		});
		libraries.setModel(libraryModel);
		dataService.getLibraryNames().forEach(l -> libraryModel.addRow(new Object[] {l}));
		
		initializePackage();
	}
	
	private void initializePackage() {
		packageModel = new DefaultTableModel();
		packageModel.setColumnIdentifiers(new Object[] {
				getResourceBundle(ResourceBundle.LABEL_PACKAGES)
		});
		packages.setModel(packageModel);
	}
	
}
