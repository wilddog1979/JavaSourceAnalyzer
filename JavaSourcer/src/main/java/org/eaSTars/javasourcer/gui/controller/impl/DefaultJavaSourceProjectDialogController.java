package org.eaSTars.javasourcer.gui.controller.impl;

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

import org.eaSTars.javasourcer.gui.context.ApplicationResources;
import org.eaSTars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eaSTars.javasourcer.gui.dto.ProjectDTO;
import org.eaSTars.javasourcer.gui.service.ApplicationGuiService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("projectdailogcontroller")
public class DefaultJavaSourceProjectDialogController extends AbstractJavaSourcerDataInputDialog<ProjectDTO> {

	private JButton buttonEdit = new JButton(ApplicationResources.THREEDOTS);
	
	private JButton buttonAdd = new JButton(ApplicationResources.PLUSSIGN);
	
	private JButton buttonDelete = new JButton(ApplicationResources.MINUSSIGN);
	
	private JTextField textFieldName = new JTextField(20);
	
	private JPanel panelDirSelection;
	
	private JLabel dirDisplay = new JLabel();
	
	private File projectdir;
	
	private DefaultListModel<String> sourceFoldersModel = new DefaultListModel<>();
	
	public DefaultJavaSourceProjectDialogController(
			MessageSource messageSource,
			ApplicationGuiService applicationGuiService) {
		super(messageSource, applicationGuiService.getLocale());
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
						createSourcesPanel()),
				3, 2, 0, 0, 0, 0);
	}
	
	private JPanel createDirSelectionPanel() {
		panelDirSelection = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton buttonDir = new JButton(getResourceBundle(ResourceBundle.PROJECTDIALOG_SELECT));
		buttonDir.addActionListener(a -> {
			JFileChooser chooser = new JFileChooser(".");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = chooser.showOpenDialog(panelDirSelection);
			switch (result) {
			case JFileChooser.APPROVE_OPTION:
				projectdir = chooser.getSelectedFile();
				dirDisplay.setText(projectdir.getName());
				break;
			default:
				projectdir = null;
				dirDisplay.setText("");
				break;
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
		scrollpane.setPreferredSize(new Dimension(200, 50));
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
	
	@Override
	protected boolean validateInputData() {
		return !(indicateError(textFieldName, StringUtils.isEmpty(textFieldName.getText())) |
				indicateError(panelDirSelection, projectdir == null));
	}

	@Override
	protected void cleanupPanel() {
		textFieldName.setText("");
		indicateError(textFieldName, false);
		dirDisplay.setText("");
		projectdir = null;
		indicateError(panelDirSelection, false);
		sourceFoldersModel.removeAllElements();
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
		dirDisplay.setText(projectdir.getName());
		indicateError(panelDirSelection, false);
		sourceFoldersModel.removeAllElements();
		parameter.getSourceFolders().forEach(sourceFoldersModel::addElement);
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
		return dto;
	}
	
}
