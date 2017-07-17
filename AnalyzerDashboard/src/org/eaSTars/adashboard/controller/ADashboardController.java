package org.eaSTars.adashboard.controller;

import java.awt.Dimension;
import java.awt.Point;

import org.eaSTars.adashboard.gui.FrameClosedListener;

public interface ADashboardController {

	public void buildMenu(boolean extended);
	
	public void start();
	
	public void addFrameClosedListener(FrameClosedListener listener);
	
	public void dispatchClosingEvent();
	
	public void showAbout();
	
	public void showPreferences();
	
	public Point getWindowLocation();
	
	public void setWindowLocation(Point point);
	
	public Dimension getWindowSize();
	
	public void setWindowSize(Dimension dimension);
	
	public Integer getDividerLocation();
	
	public void setDividerLocation(Integer dividerlocation);
	
	public void saveSettings();
}
