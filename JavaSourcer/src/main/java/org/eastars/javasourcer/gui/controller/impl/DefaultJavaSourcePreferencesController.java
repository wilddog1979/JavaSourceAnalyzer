package org.eastars.javasourcer.gui.controller.impl;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.eastars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eastars.javasourcer.gui.controller.DialogPanelController;
import org.eastars.javasourcer.gui.service.ApplicationGuiService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("preferencesdailogcontroller")
public class DefaultJavaSourcePreferencesController extends AbstractDialogController {

	private DefaultPreferencesTopicModel topicModel;
	
	private JScrollPane scrollPaneSettings = new JScrollPane();
	
	private Map<String, DialogPanelController> dialogPanelControllers = new HashMap<>();
	
	public DefaultJavaSourcePreferencesController(
			MessageSource messageSource,
			ApplicationGuiService applicationGuiService,
			@Qualifier("javalibrarydialogpanelcontroller") DialogPanelController javaLibraryDialogController) {
		super(messageSource, applicationGuiService.getLocale());
		
		dialogPanelControllers.put(ResourceBundle.TOPIC_LIBRARIES, javaLibraryDialogController);
	}

	@Override
	public boolean showDialog(Frame parent) {
		dialogPanelControllers.values().forEach(DialogPanelController::initializeContent);
		
		return super.showDialog(parent);
	}
	
	@Override
	protected String getTitle() {
		return getResourceBundle(ResourceBundle.TITLE);
	}
	
	@Override
	protected JPanel buildPanel() {
		JPanel panelSettings = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		topicModel = new DefaultPreferencesTopicModel(new String[][] {
			{ResourceBundle.TOPIC_LIBRARIES, getResourceBundle(ResourceBundle.TOPIC_LIBRARIES)}
		});
		JList<String> topics = new JList<>(topicModel);
		topics.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane spTopics = new JScrollPane(topics);
		spTopics.setPreferredSize(new Dimension(100, 200));
		panelSettings.add(spTopics);
		
		updateSettingPanel(0);
		scrollPaneSettings.setPreferredSize(new Dimension(350, 210));
		panelSettings.add(scrollPaneSettings);
		
		topics.addListSelectionListener(l -> updateSettingPanel(topics.getSelectedIndex()));
		
		return panelSettings;
	}
	
	private void updateSettingPanel(int index) {
		String key = topicModel.getKeyAt(index);
		scrollPaneSettings.setViewportView(dialogPanelControllers.get(key).getPanel());
	}
	
	@Override
	protected void dialogAction(boolean result) {
		if (result) {
			dialogPanelControllers.values().forEach(DialogPanelController::saveContent);
		}
		super.dialogAction(result);
	}

}
