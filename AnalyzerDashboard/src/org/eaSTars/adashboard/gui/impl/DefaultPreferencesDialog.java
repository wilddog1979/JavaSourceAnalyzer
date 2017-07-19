package org.eaSTars.adashboard.gui.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.eaSTars.adashboard.gui.PreferencesDialog;
import org.oracle.swing.layout.SpringUtilities;

public class DefaultPreferencesDialog implements PreferencesDialog {

	private static final String ORDERED_SEQUENCE = "Ordered sequence";
	
	private static final String INCLUDE_RETURN_LABELS = "Include return labels";
	
	private static final Map<String, JCheckBox> SETTINGS = Arrays
			.asList(ORDERED_SEQUENCE, INCLUDE_RETURN_LABELS)
			.stream().collect(Collectors.toMap(k -> k, k -> new JCheckBox()));
	
	private JPanel settingspanel;
	
	@Override
	public boolean showDialog() {
		if (settingspanel == null) {
			settingspanel = buildSettingPanel();
		}
		
		return JOptionPane.showConfirmDialog(null, settingspanel, "Preferences", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
	}

	private JPanel buildSettingPanel() {
		JPanel mainpanel = new JPanel();
		
		JPanel sequencesettingpanel = new JPanel(new SpringLayout());
		sequencesettingpanel.setBorder(BorderFactory.createTitledBorder("Sequence graph"));
		SETTINGS.entrySet().forEach(s -> {
			JLabel l = new JLabel(s.getKey());
			sequencesettingpanel.add(l);
			l.setLabelFor(s.getValue());
			sequencesettingpanel.add(s.getValue());
		});
		SpringUtilities.makeCompactGrid(sequencesettingpanel, SETTINGS.size(), 2, 6, 6, 6, 6);
		
		mainpanel.add(sequencesettingpanel);
		
		return mainpanel;
	}

	@Override
	public boolean getOrderedSequence() {
		return SETTINGS.get(ORDERED_SEQUENCE).isSelected();
	}

	@Override
	public void setOrderedSequence(boolean value) {
		SETTINGS.get(ORDERED_SEQUENCE).setSelected(value);
	}

	@Override
	public boolean getIncludeReturnLabels() {
		return SETTINGS.get(INCLUDE_RETURN_LABELS).isSelected();
	}

	@Override
	public void setIncludeReturnLabels(boolean value) {
		SETTINGS.get(INCLUDE_RETURN_LABELS).setSelected(value);
	}
}
