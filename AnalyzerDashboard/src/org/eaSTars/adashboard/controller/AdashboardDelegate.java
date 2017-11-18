package org.eaSTars.adashboard.controller;

import java.awt.Dimension;
import java.awt.Point;

public interface AdashboardDelegate {

	public Point getWindowLocation();
	
	public void setWindowLocation(Point point);
	
	public Dimension getWindowSize();
	
	public void setWindowSize(Dimension dimension);
	
	public Integer getDividerLocation();
	
	public void setDividerLocation(Integer dividerlocation);
	
	public boolean getOrderSequence();
	
	public void setOrderedSequence(boolean value);
	
	public boolean getIncludeReturnLabels();
	
	public void saveSettings();
	
	public void showAbout();
	
	public void showPreferences();
}
