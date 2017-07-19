package org.eaSTars.adashboard.gui;

public interface MainFrame {
	
	public void setVisible(boolean value);
	
	public void dispatchClosingEvent();
	
	public void buildMenu(boolean extended);
	
	public void redrawSequence();
	
	public void updateOrderChecker(boolean value);
}
