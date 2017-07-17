package org.eaSTars.adashboard.controller;

import org.eaSTars.adashboard.gui.FrameClosedListener;

public interface ADashboardController {

	public void buildMenu(boolean extended);
	
	public void start();
	
	public void addFrameClosedListener(FrameClosedListener listener);
	
	public void dispatchClosingEvent();
	
	public void showAbout();
	
	public void showPreferences();
}
