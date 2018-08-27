package org.eastars.javasourcer.gui.controller.impl;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import org.eastars.javasourcer.data.service.JavaSourcerDataService;
import org.eastars.javasourcer.gui.context.ApplicationResources;
import org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eastars.javasourcer.gui.dto.ProjectDTO;
import org.eastars.javasourcer.gui.service.ApplicationGuiService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("projectdailogcontroller")
public class DefaultJavaSourceProjectDialogController extends AbstractJavaSourcerDataInputDialog<ProjectDTO> {

	private JButton buttonEdit = new JButton(ApplicationResources.THREEDOTS);
	
	private JButton buttonAdd = new JButton(ApplicationResources.PLUSSIGN);
	
	private JButton buttonDelete = new JButton(ApplicationResources.MINUSSIGN);
	
	private JButton buttonRemoveLibrary = new JButton(ApplicationResources.ARROWRIGHT);
	
	private JButton buttonAddLibrary = new JButton(ApplicationResources.ARROWLEFT);
	
	private JTextField textFieldName = new JTextField(20);
	
	private JPanel panelDirSelection;
	
	private JLabel dirDisplay = new JLabel();
	
	private File projectdir;
	
	private DefaultListModel<String> sourceFoldersModel = new DefaultListModel<>();
	
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
				4, 2, 0, 0, 0, 0);
	}
	
	private JPanel createDirSelectionPanel() {
		panelDirSelection = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
		
		JList<String> sourcelist = new JList<>(sourceFoldersModel);
		
		sourcelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sourcelist.setVisibleRowCount(3);
		
		JScrollPane scrollpane = new JScrollPane(sourcelist);
		scrollpane.setPreferredSize(new Dimension(400, 50));
		sources.add(scrollpane);
		
		JToolBar panelButtons = new JToolBar(JToolBar.VERTICAL);
		panelButtons.setFloatable(false);
		
		buttonEdit.addActionListener(a -> {
			int index = sourcelist.getSelectedIndex();
			if (index != -1) {
				JFileChooser editFolder = new JFileChooser(projectdir != null ?
						new File(projectdir, sourceFoldersModel.getElementAt(index)).getParentFile() :
							new File("."));
				editFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (editFolder.showOpenDialog(sources) == JFileChooser.APPROVE_OPTION) {
					sourceFoldersModel.removeElementAt(index);
					sourceFoldersModel.insertElementAt(Paths.get(projectdir.toURI())
							.relativize(Paths.get(editFolder.getSelectedFile().toURI())).toString(), index);
				}
			}
		});
		panelButtons.add(buttonEdit);
		
		buttonAdd.addActionListener(a -> {
			JFileChooser addFolder = new JFileChooser(projectdir != null ? projectdir : new File("."));
			addFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (addFolder.showOpenDialog(sources) == JFileChooser.APPROVE_OPTION) {
				sourceFoldersModel.addElement(Paths.get(projectdir.toURI())
						.relativize(Paths.get(addFolder.getSelectedFile().toURI())).toString());
			}
		});
		panelButtons.add(buttonAdd);
		
		buttonDelete.addActionListener(a -> {
			int index = sourcelist.getSelectedIndex();
			if (index != -1) {
				sourceFoldersModel.removeElementAt(index);
			}
		});
		panelButtons.add(buttonDelete);
		
		sources.add(panelButtons);
		
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
		
		sourceFoldersModel.removeAllElements();
		
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
		
		sourceFoldersModel.removeAllElements();
		parameter.getSourceFolders().forEach(sourceFoldersModel::addElement);
		
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
		
		dto.setSourceFolders(Collections.list(sourceFoldersModel.elements()));
		
		dto.setLibraries(Collections.list(selectedLibrariesModel.elements()));
		return dto;
	}
	
}
