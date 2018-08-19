package org.eaSTars.javasourcer.gui.controller.impl;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.eaSTars.javasourcer.data.service.JavaSourcerDataService;
import org.eaSTars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eaSTars.javasourcer.gui.dto.LibraryDTO;
import org.eaSTars.javasourcer.gui.service.ApplicationGuiService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("javalibrarydialogcontroller")
public class DefaultJavaLibrariesDialogController extends AbstractDialogController {

	private JavaSourcerDataService dataService;
	
	private LibraryTableModel libraryModel;
	
	private PackageTableModel packageModel;
	
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
		
		libraryModel = new LibraryTableModel();
		libraryModel.setColumnNames(Arrays.asList(
				getResourceBundle(ResourceBundle.LABEL_LIBRARIES)));
		JTable libraries = new JTable(libraryModel);
		
		javaLibraryPanel.add(buildTableWithButton(libraries, 100, a -> {
			LibraryDTO dto = new LibraryDTO();
			dto.setName(getResourceBundle(ResourceBundle.NEW_ENTRY));
			libraryModel.addNewLibrary(dto);
			int index = libraryModel.getRowCount();
			libraries.scrollRectToVisible(libraries.getCellRect(index - 1, 0, true));
			libraries.editCellAt(index - 1, 0);
		}));
		
		packageModel = new PackageTableModel();
		packageModel.setColumnNames(Arrays.asList(
				getResourceBundle(ResourceBundle.LABEL_PACKAGES)));
		JTable packages = new JTable(packageModel);
		
		javaLibraryPanel.add(buildTableWithButton(packages, 200, a -> {
			packageModel.addNewPackage(getResourceBundle(ResourceBundle.NEW_ENTRY));
			int index = packageModel.getRowCount();
			packages.scrollRectToVisible(packages.getCellRect(index - 1, 0, true));
			packages.editCellAt(index - 1, 0);
		}));
		
		libraries.getSelectionModel().addListSelectionListener(l -> {
			packageModel.setPackages(new ArrayList<>());
			int index = libraries.getSelectedRow();
			if (index != -1) {
				LibraryDTO dto = libraryModel.getLibraries().get(index);
				if (dto != null) {
					packageModel.setPackages(dto.getPackages());
				}
			}
		});
		
		return javaLibraryPanel;
	}

	private JPanel buildTableWithButton(JTable table, int width, ActionListener actionListener) {
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
		libraryModel.setLibraries(dataService.getLibraries());
		
		packageModel.setPackages(new ArrayList<>());
	}
	
}
