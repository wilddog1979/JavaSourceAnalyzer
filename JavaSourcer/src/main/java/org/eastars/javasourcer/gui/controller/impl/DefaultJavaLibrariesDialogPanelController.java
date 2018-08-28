package org.eastars.javasourcer.gui.controller.impl;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;

import org.eastars.javasourcer.data.service.JavaSourcerDataService;
import org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eastars.javasourcer.gui.controller.DialogPanelController;
import org.eastars.javasourcer.gui.dto.LibraryDTO;
import org.eastars.javasourcer.gui.service.ApplicationGuiService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("javalibrarydialogpanelcontroller")
public class DefaultJavaLibrariesDialogPanelController extends AbstractDialogPanelController implements DialogPanelController {

	private JavaSourcerDataService dataService;
	
	private JavaSourcerTableModel<LibraryDTO> libraryModel = new JavaSourcerTableModel<>(
			true,
			(e, c) -> e.getName(), 
			(e, c, v) -> e.setName(v.toString())) ;
	
	private JavaSourcerTableModel<String> packageModel = new JavaSourcerTableModel<>(
			true,
			(e, c) -> e,
			(e, c, v) -> v.toString());
	
	private List<String> libraryRemove = new ArrayList<>();
	
	public DefaultJavaLibrariesDialogPanelController(
			MessageSource messageSource,
			ApplicationGuiService applicationGuiService,
			JavaSourcerDataService dataService) {
		super(messageSource, applicationGuiService.getLocale());
		this.dataService = dataService;
	}

	@Override
	protected JPanel buildPanel() {
		JPanel javaLibraryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		libraryModel.setColumnNames(Arrays.asList(
				getResourceBundle(ResourceBundle.LABEL_LIBRARIES)));
		JTable libraries = new JTable(libraryModel);
		
		javaLibraryPanel.add(buildTableWithButtons(libraries, 100, 150, a -> {
			LibraryDTO dto = new LibraryDTO();
			dto.setName(getResourceBundle(ResourceBundle.NEW_ENTRY));
			libraryModel.addNewEntry(dto);
			int index = libraryModel.getRowCount();
			libraries.scrollRectToVisible(libraries.getCellRect(index - 1, 0, true));
			libraries.editCellAt(index - 1, 0);
		}, a -> {
			int index = libraries.getSelectedRow();
			if (index != -1) {
				libraryRemove.add(libraryModel.getEntries().get(index).getOriginalName());
				libraryModel.removeEntry(index);
			}
		}));
		
		packageModel.setColumnNames(Arrays.asList(
				getResourceBundle(ResourceBundle.LABEL_PACKAGES)));
		JTable packages = new JTable(packageModel);
		
		javaLibraryPanel.add(buildTableWithButtons(packages, 200, 150, a -> {
			if (libraries.getSelectedRow() != -1) {
				packageModel.addNewEntry(getResourceBundle(ResourceBundle.NEW_ENTRY));
				int index = packageModel.getRowCount();
				packages.scrollRectToVisible(packages.getCellRect(index - 1, 0, true));
				packages.editCellAt(index - 1, 0);
			}
		}, a -> {
			int index = packages.getSelectedRow();
			if (index != -1) {
				packageModel.removeEntry(index);
			}
		}));
		
		libraries.getSelectionModel().addListSelectionListener(l -> {
			packageModel.setEntries(new ArrayList<>());
			int index = libraries.getSelectedRow();
			if (index != -1) {
				LibraryDTO dto = libraryModel.getEntries().get(index);
				if (dto != null) {
					packageModel.setEntries(dto.getPackages());
				}
			}
		});
		
		return javaLibraryPanel;
	}

	@Override
	public void initializeContent() {
		libraryModel.setEntries(dataService.getLibraries());
		
		packageModel.setEntries(new ArrayList<>());
		
		libraryRemove.clear();
	}
	
	@Override
	public boolean saveContent() {
		libraryRemove.forEach(dataService::deleteLibrary);
		
		libraryModel.getEntries().forEach(dataService::saveLibrary);
		
		return false;
	}
	
}
