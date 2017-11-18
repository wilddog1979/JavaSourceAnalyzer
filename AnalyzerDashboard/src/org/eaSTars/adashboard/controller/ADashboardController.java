package org.eaSTars.adashboard.controller;

import org.eaSTars.adashboard.gui.FrameClosedListener;

public interface ADashboardController extends AdashboardDelegate {

	public void buildMenu(boolean extended);
	
	public void start();
	
	public void addFrameClosedListener(FrameClosedListener listener);
	
	public void dispatchClosingEvent();
}
