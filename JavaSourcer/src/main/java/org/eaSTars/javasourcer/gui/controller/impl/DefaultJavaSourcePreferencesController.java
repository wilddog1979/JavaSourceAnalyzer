package org.eaSTars.javasourcer.gui.controller.impl;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;

import org.eaSTars.javasourcer.gui.context.ApplicationResources.ResourceBundle;
import org.eaSTars.javasourcer.gui.controller.DialogPanelController;
import org.eaSTars.javasourcer.gui.controller.JavaSourcerDialog;
import org.eaSTars.javasourcer.gui.service.ApplicationGuiService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component("preferencesdailogcontroller")
public class DefaultJavaSourcePreferencesController extends AbstractInternationalizableController implements JavaSourcerDialog {

	private JDialog dialog;
	
	private DefaultPreferencesTopicModel topicModel;
	
	private JScrollPane scrollPaneSettings = new JScrollPane();
	
	private Map<String, DialogPanelController> dialogPanelControllers = new HashMap<>();
	
	private boolean result;
	
	public DefaultJavaSourcePreferencesController(
			MessageSource messageSource,
			ApplicationGuiService applicationGuiService,
			@Qualifier("javalibrarydialogcontroller") DialogPanelController javaLibraryDialogController) {
		super(messageSource, applicationGuiService.getLocale());
		
		dialogPanelControllers.put(ResourceBundle.TOPIC_LIBRARIES, javaLibraryDialogController);
	}

	@Override
	public boolean showDialog(Frame parent) {
		if (dialog == null) {
			buildDialog(parent);
		}
		
		Dimension dialogsize = dialog.getSize();
		Rectangle parentbounds = parent.getBounds();
		dialog.setLocation(
				Math.max(parentbounds.x + parentbounds.getSize().width / 2 - dialogsize.width / 2, 0),
				Math.max(parentbounds.y + parentbounds.getSize().height / 2 - dialogsize.height / 2, 0));
		
		dialogPanelControllers.values().forEach(DialogPanelController::initializeContent);
		
		dialog.setVisible(true);
		
		return result;
	}

	private void buildDialog(Frame parent) {
		dialog = new JDialog(parent, getResourceBundle(ResourceBundle.TITLE), true);
		
		dialog.add(makeCompactGrid(Arrays.asList(
				buildSettingsPanel(),
				buildButtonPanel()),
				2, 1, 0, 0, 0, 0));
		
		dialog.pack();
		dialog.setResizable(false);
	}
	
	private JPanel buildSettingsPanel() {
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
	
	private JPanel buildButtonPanel() {
		JPanel panelBottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton buttonOK = new JButton(getResourceBundle(ResourceBundle.BUTTON_OK));
		buttonOK.addActionListener(a -> dialogAction(true));
		panelBottomButtons.add(buttonOK);
		
		JButton buttonCancel = new JButton(getResourceBundle(ResourceBundle.BUTTON_CANCEL));
		buttonCancel.addActionListener(a -> dialogAction(false));
		panelBottomButtons.add(buttonCancel);
		
		registerHotKeyAction(dialog.getRootPane(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "dialogyes", e -> dialogAction(true));
		registerHotKeyAction(dialog.getRootPane(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "dialogno", e -> dialogAction(false));
		
		return panelBottomButtons;
	}
	
	private void dialogAction(boolean result) {
		dialog.dispose();
		this.result = result;
	}

}
