package org.eaSTars.javasourcer.gui.controller.impl;

import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eaSTars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eaSTars.javasourcer.gui.dto.CreateProjectDTO;
import org.eaSTars.javasourcer.gui.service.ApplicationGuiService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("createprojectdailogcontroller")
public class DefaultJavaSourceCreateProjectDialogController extends AbstractJavaSourcerDataInputDialog<CreateProjectDTO> {

	private JTextField textFieldName = new JTextField(20);
	
	JPanel panelDirSelection;
	
	JLabel dirDisplay = new JLabel();
	
	private File projectdir;
	
	public DefaultJavaSourceCreateProjectDialogController(
			MessageSource messageSource,
			ApplicationGuiService applicationGuiService) {
		super(messageSource, applicationGuiService.getLocale());
	}
	
	@Override
	protected String getTitle() {
		return getResourceBundle(ResourceBundle.TITLE);
	}

	@Override
	protected JPanel buildPanel() {
		JPanel mainpanel = new JPanel();
		
		JLabel labelName = new JLabel(getResourceBundle(ResourceBundle.CREATEPROJECT_DIALOG_NAME));
		labelName.setLabelFor(textFieldName);
		
		panelDirSelection = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel labelDir = new JLabel(getResourceBundle(ResourceBundle.CREATEPROJECT_DIALOG_DIRECTORY));
		JButton buttonDir = new JButton(getResourceBundle(ResourceBundle.CREATEPROJECT_DIALOG_SELECT));
		buttonDir.addActionListener(a -> {
			JFileChooser chooser = new JFileChooser(".");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = chooser.showOpenDialog(mainpanel);
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
		
		JPanel sequencesettingpanel = makeCompactGrid(
				Arrays.asList(
						labelName, textFieldName,
						labelDir, panelDirSelection),
				2, 2, 6, 6, 6, 6);
		
		mainpanel.add(sequencesettingpanel);
		
		return mainpanel;
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
	}

	@Override
	protected void initializePanel(CreateProjectDTO parameter) {
		textFieldName.setText(parameter.getName());
		projectdir = new File(parameter.getBasedir());
		dirDisplay.setText(projectdir.getName());
	}
	
	@Override
	protected CreateProjectDTO getInputData() {
		CreateProjectDTO dto = new CreateProjectDTO();
		dto.setName(textFieldName.getText().trim());
		try {
			dto.setBasedir(projectdir.getCanonicalPath());
		} catch (IOException e) {
			// this may not be an issue
		}
		return dto;
	}
	
}
