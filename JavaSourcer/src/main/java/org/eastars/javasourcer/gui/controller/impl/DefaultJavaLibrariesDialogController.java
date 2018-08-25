package org.eastars.javasourcer.gui.controller.impl;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.eastars.javasourcer.data.service.JavaSourcerDataService;
import org.eastars.javasourcer.gui.context.ApplicationResources;
import org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eastars.javasourcer.gui.dto.LibraryDTO;
import org.eastars.javasourcer.gui.service.ApplicationGuiService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("javalibrarydialogcontroller")
public class DefaultJavaLibrariesDialogController extends AbstractDialogController {

	private JavaSourcerDataService dataService;
	
	private LibraryTableModel libraryModel;
	
	private PackageTableModel packageModel;
	
	private List<String> libraryRemove = new ArrayList<>();
	
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
		}, a -> {
			int index = libraries.getSelectedRow();
			if (index != -1) {
				libraryRemove.add(libraryModel.getLibraries().get(index).getOriginalName());
				libraryModel.removeLibrary(index);
			}
		}));
		
		packageModel = new PackageTableModel();
		packageModel.setColumnNames(Arrays.asList(
				getResourceBundle(ResourceBundle.LABEL_PACKAGES)));
		JTable packages = new JTable(packageModel);
		
		javaLibraryPanel.add(buildTableWithButton(packages, 200, a -> {
			if (libraries.getSelectedRow() != -1) {
				packageModel.addNewPackage(getResourceBundle(ResourceBundle.NEW_ENTRY));
				int index = packageModel.getRowCount();
				packages.scrollRectToVisible(packages.getCellRect(index - 1, 0, true));
				packages.editCellAt(index - 1, 0);
			}
		}, a -> {
			int index = packageModel.getRowCount();
			if (index != -1) {
				packageModel.removePackage(index);
			}
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

	private JPanel buildTableWithButton(JTable table, int width, ActionListener addAction, ActionListener removeAction) {
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPaneLibraries = new JScrollPane(table);
		scrollPaneLibraries.setPreferredSize(new Dimension(width, 150));
		
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
	
	@Override
	public void initializeContent() {
		libraryModel.setLibraries(dataService.getLibraries());
		
		packageModel.setPackages(new ArrayList<>());
		
		libraryRemove.clear();
	}
	
	@Override
	public boolean saveContent() {
		libraryRemove.forEach(dataService::deleteLibrary);
		
		libraryModel.getLibraries().forEach(dataService::saveLibrary);
		
		return false;
	}
	
}
