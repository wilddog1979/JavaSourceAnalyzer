package org.eaSTars.adashboard.gui;

import org.eaSTars.adashboard.controller.ADashboardController;

public interface MainFrame {
	
	public void setVisible(boolean value);
	
	public void dispatchClosingEvent();
	
	public void buildMenu(boolean extended, ADashboardController controller);
}
