package org.eaSTars.adashboard.gui;

public interface PreferencesDialog extends ADashboardDialog {

	public boolean getOrderedSequence();
	
	public void setOrderedSequence(boolean value);
	
	public boolean getIncludeReturnLabels();
	
	public void setIncludeReturnLabels(boolean value);
}
