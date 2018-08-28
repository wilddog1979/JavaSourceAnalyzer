package org.eastars.javasourcer.gui.controller.impl;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.eastars.javasourcer.data.service.JavaSourcerDataService;
import org.eastars.javasourcer.gui.context.ApplicationResources;
import org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eastars.javasourcer.gui.dto.ModuleDTO;
import org.eastars.javasourcer.gui.dto.ProjectDTO;
import org.eastars.javasourcer.gui.service.ApplicationGuiService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("projectdailogcontroller")
public class DefaultJavaSourceProjectDialogController extends AbstractDataInputDialogController<ProjectDTO> {

	private JButton buttonRemoveLibrary = new JButton(ApplicationResources.ARROWRIGHT);
	
	private JButton buttonAddLibrary = new JButton(ApplicationResources.ARROWLEFT);
	
	private JTextField textFieldName = new JTextField(20);
	
	private JPanel panelDirSelection = new JPanel(new FlowLayout(FlowLayout.LEFT));
	
	private JLabel dirDisplay = new JLabel();

	private File projectdir;

	private JavaSourcerTableModel<ModuleDTO> moduleModel = new JavaSourcerTableModel<>(
			true,
			(e, c) -> e.getName(),
			(e, c, v) -> e.setName(v.toString()));

	private JavaSourcerTableModel<String> sourceFolderModel = new JavaSourcerTableModel<>(
			false,
			(e, c) -> e,
			(e, c, v) -> v.toString());

	private List<String> modulesRemove = new ArrayList<>();

	private DefaultListModel<String> selectedLibrariesModel = new DefaultListModel<>();

	private DefaultListModel<String> availableLibrariesModel = new DefaultListModel<>();

	private JavaSourcerDataService dataService;
	
	public DefaultJavaSourceProjectDialogController(
			MessageSource messageSource,
			ApplicationGuiService applicationGuiService,
			JavaSourcerDataService dataService) {
		super(messageSource, applicationGuiService.getLocale());
		this.dataService = dataService;
	}

	@Override
	protected JPanel buildPanel() {
		return makeCompactGrid(
				Arrays.asList(
						new JLabel(getResourceBundle(ResourceBundle.ROJECTDIALOG_NAME)),
						textFieldName,
						new JLabel(getResourceBundle(ResourceBundle.PROJECTDIALOG_DIRECTORY)),
						createDirSelectionPanel(),
						new JLabel(getResourceBundle(ResourceBundle.PROJECTDIALOG_SOURCES)),
						createSourcesPanel(),
						new JLabel(getResourceBundle(ResourceBundle.PROJECTDIALOG_LIBRARIES)),
						createLibrariesPanel()),
				4, 2, 4, 4, 4, 4);
	}
	
	private JPanel createDirSelectionPanel() {
		JButton buttonDir = new JButton(getResourceBundle(ResourceBundle.PROJECTDIALOG_SELECT));
		buttonDir.addActionListener(a -> {
			JFileChooser chooser = new JFileChooser(".");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = chooser.showOpenDialog(panelDirSelection);
			if (result == JFileChooser.APPROVE_OPTION) {
				projectdir = chooser.getSelectedFile();
				dirDisplay.setText(projectdir.getName());
			} else {
				projectdir = null;
				dirDisplay.setText("");
			}
		});
		panelDirSelection.add(buttonDir);
		panelDirSelection.add(dirDisplay);
		
		return panelDirSelection;
	}
	
	private JPanel createSourcesPanel() {
		JPanel sources = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		moduleModel.setColumnNames(Arrays.asList(
				getResourceBundle(ResourceBundle.LABEL_MODULES)));
		JTable modules = new JTable(moduleModel);
		
		sources.add(buildTableWithButtons(modules, 100, 150, a -> {
			ModuleDTO dto = new ModuleDTO();
			dto.setName(getResourceBundle(ResourceBundle.NEW_ENTRY));
			moduleModel.addNewEntry(dto);
			int index = moduleModel.getRowCount();
			modules.scrollRectToVisible(modules.getCellRect(index - 1, 0, true));
			modules.editCellAt(index - 1, 0);
		}, a -> {
			int index = modules.getSelectedRow();
			if (index != -1) {
				modulesRemove.add(moduleModel.getEntries().get(index).getOriginalName());
				moduleModel.removeEntry(index);
			}
		}));
		
		sourceFolderModel.setColumnNames(Arrays.asList(
				getResourceBundle(ResourceBundle.LABEL_SOURCEFOLDERS)));
		JTable sourceFolders = new JTable(sourceFolderModel);
		
		sources.add(buildTableWithButtons(sourceFolders, 250, 150, a -> {
			// this must be implemented properly with folder selection
			sourceFolderModel.addNewEntry(getResourceBundle(ResourceBundle.NEW_ENTRY));
			int index = sourceFolderModel.getRowCount();
			sourceFolders.scrollRectToVisible(sourceFolders.getCellRect(index - 1, 0, true));
			sourceFolders.editCellAt(index - 1, 0);
		}, a -> {
			int index = sourceFolders.getSelectedRow();
			if (index != -1) {
				sourceFolderModel.removeEntry(index);
			}
		}, a -> {
			int index = sourceFolders.getSelectedRow();
			if (index != -1) {
				// this must be implemented properly with folder selection
			}
		}));
		
		modules.getSelectionModel().addListSelectionListener(l -> {
			sourceFolderModel.setEntries(new ArrayList<>());
			int index = modules.getSelectedRow();
			if (index != -1) {
				sourceFolderModel.setEntries(moduleModel.getEntries().get(index).getSourceFolders());
			}
		});
		
		return sources;
	}
	
	private JPanel createLibrariesPanel() {
		JPanel libraries = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JList<String> selectedLibrarieslist = new JList<>(selectedLibrariesModel);
		JScrollPane scrollpane1 = new JScrollPane(selectedLibrarieslist);
		scrollpane1.setPreferredSize(new Dimension(200, 50));
		libraries.add(scrollpane1);
		
		JToolBar panelButtons = new JToolBar(JToolBar.VERTICAL);
		panelButtons.setFloatable(false);
		
		panelButtons.add(buttonAddLibrary);
		
		panelButtons.add(buttonRemoveLibrary);
		
		libraries.add(panelButtons);
		
		JList<String> availableLibrarieslist = new JList<>(availableLibrariesModel);
		JScrollPane scrollpane2 = new JScrollPane(availableLibrarieslist);
		scrollpane2.setPreferredSize(new Dimension(200, 50));
		libraries.add(scrollpane2);
		
		buttonAddLibrary.addActionListener(a -> availableLibrarieslist.getSelectedValuesList().forEach(l -> {
			availableLibrariesModel.removeElement(l);
			selectedLibrariesModel.addElement(l);
		}));
		
		buttonRemoveLibrary.addActionListener(a -> selectedLibrarieslist.getSelectedValuesList().forEach(l -> {
			selectedLibrariesModel.removeElement(l);
			availableLibrariesModel.addElement(l);
		}));
		
		return libraries;
	}
	
	@Override
	protected boolean validateInputData() {
		boolean result = indicateError(textFieldName, StringUtils.isEmpty(textFieldName.getText()));
		
		result |= indicateError(panelDirSelection, projectdir == null);
		
		return !result;
	}

	@Override
	protected void cleanupPanel() {
		textFieldName.setText("");
		indicateError(textFieldName, false);
		
		dirDisplay.setText("");
		projectdir = null;
		indicateError(panelDirSelection, false);
		
		moduleModel.setEntries(new ArrayList<>());
		modulesRemove.clear();
		
		sourceFolderModel.setEntries(new ArrayList<>());
		
		selectedLibrariesModel.removeAllElements();
		
		availableLibrariesModel.removeAllElements();
		dataService.getLibraryNames().forEach(availableLibrariesModel::addElement);
	}

	@Override
	protected void initializePanel(ProjectDTO parameter) {
		textFieldName.setText(parameter.getName());
		indicateError(textFieldName, false);
		
		try {
			projectdir = new File(parameter.getBasedir()).getCanonicalFile();
			dirDisplay.setText(projectdir.getName());
		} catch (IOException e) {
			projectdir = null;
			dirDisplay.setText("");
		}
		indicateError(panelDirSelection, false);
		
		moduleModel.setEntries(parameter.getModules());
		modulesRemove.clear();
		
		sourceFolderModel.setEntries(new ArrayList<>());
		
		selectedLibrariesModel.removeAllElements();
		parameter.getLibraries().forEach(selectedLibrariesModel::addElement);
		
		availableLibrariesModel.removeAllElements();
		dataService.getLibraryNames()
		.filter(l -> !parameter.getLibraries().contains(l))
		.forEach(availableLibrariesModel::addElement);
	}
	
	@Override
	protected ProjectDTO getInputData() {
		ProjectDTO dto = new ProjectDTO();
		
		dto.setName(textFieldName.getText().trim());
		
		try {
			dto.setBasedir(projectdir.getCanonicalPath());
		} catch (IOException e) {
			// this may not be an issue
		}
		
		dto.setModules(moduleModel.getEntries());
		
		dto.setLibraries(Collections.list(selectedLibrariesModel.elements()));
		return dto;
	}
	
}
