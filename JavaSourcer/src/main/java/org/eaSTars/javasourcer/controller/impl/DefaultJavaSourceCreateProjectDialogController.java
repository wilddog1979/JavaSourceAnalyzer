package org.eaSTars.javasourcer.controller.impl;

import static org.eaSTars.javasourcer.configuration.ApplicationResources.ResourceBundle.*;

import java.util.Arrays;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eaSTars.javasourcer.dto.CreateProjectDTO;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("createprojectdailogcontroller")
public class DefaultJavaSourceCreateProjectDialogController extends AbstractJavaSourcerDataInputDialog<CreateProjectDTO> {

	private JTextField nameTextField = new JTextField(20);
	
	public DefaultJavaSourceCreateProjectDialogController(MessageSource messageSource, Locale locale) {
		super(messageSource, locale);
	}
	
	@Override
	protected String getTitle() {
		return getResourceBundle(TITLE);
	}

	@Override
	protected JPanel buildPanel() {
		JPanel mainpanel = new JPanel();
		
		JLabel nameLabel = new JLabel(getResourceBundle(CREATEPROJECT_DIALOG_NAME));
		nameLabel.setLabelFor(nameTextField);
		JPanel sequencesettingpanel = makeCompactGrid(Arrays.asList(nameLabel, nameTextField), 1, 2, 6, 6, 6, 6);
		
		mainpanel.add(sequencesettingpanel);
		
		return mainpanel;
	}

	@Override
	protected void cleanupPanel() {
		nameTextField.setText("");
	}

	@Override
	protected void initializePanel(CreateProjectDTO parameter) {
		nameTextField.setText(parameter.getName());
	}
	
	@Override
	protected CreateProjectDTO getInputData() {
		CreateProjectDTO dto = new CreateProjectDTO();
		dto.setName(nameTextField.getText().trim());
		return dto;
	}
	
}
