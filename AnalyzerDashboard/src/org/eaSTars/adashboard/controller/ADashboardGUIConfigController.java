package org.eaSTars.adashboard.controller;

import java.awt.Dimension;
import java.awt.Point;

public interface ADashboardGUIConfigController {

	public Point getWindowLocation();
	
	public void setWindowLocation(Point point);
	
	public Dimension getWindowSize();
	
	public void setWindowSize(Dimension dimension);
	
	public Integer getDividerLocation();
	
	public void setDividerLocation(Integer dividerlocation);
	
	public void saveSettings();
}
