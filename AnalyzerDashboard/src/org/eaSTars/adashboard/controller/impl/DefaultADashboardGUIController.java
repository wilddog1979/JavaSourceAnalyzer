package org.eaSTars.adashboard.controller.impl;

import java.awt.Dimension;
import java.awt.Point;

import org.eaSTars.adashboard.controller.ADashboardGUIConfigController;
import org.eaSTars.util.ConfigService;

public class DefaultADashboardGUIController implements ADashboardGUIConfigController {

	private static final String GUI_X = "adashboard.gui.x";
	
	private static final String GUI_Y = "adashboard.gui.y";
	
	private static final String GUI_WIDTH = "adashboard.gui.width";
	
	private static final String GUI_HEIGHT = "adashboard.gui.height";
	
	private ConfigService guiConfigService;

	@Override
	public Point getWindowLocation() {
		Integer x = getIntegerValueFromConfig(GUI_X);
		Integer y = getIntegerValueFromConfig(GUI_Y);
		
		if (x != null && y != null) {
			return new Point(x, y);
		}
		return null;
	}
	
	@Override
	public void setWindowLocation(Point point) {
		guiConfigService.setProperty(GUI_X, Integer.toString(point.x));
		guiConfigService.setProperty(GUI_Y, Integer.toString(point.y));
	}
	
	@Override
	public Dimension getWindowSize() {
		Integer width = getIntegerValueFromConfig(GUI_WIDTH);
		Integer height = getIntegerValueFromConfig(GUI_HEIGHT);
		
		if (width != null && height != null) {
			return new Dimension(width, height);
		}
		
		return null;
	}
	
	@Override
	public void setWindowSize(Dimension dimension) {
		guiConfigService.setProperty(GUI_WIDTH, Integer.toString(dimension.width));
		guiConfigService.setProperty(GUI_HEIGHT, Integer.toString(dimension.height));
	}
	
	@Override
	public void saveSettings() {
		guiConfigService.saveConfig();
	}
	
	private Integer getIntegerValueFromConfig(String key) {
		String rawvalue = guiConfigService.getProperty(key);
		if (rawvalue != null) {
			try {
				return Integer.parseInt(rawvalue);
			} catch (NumberFormatException e) {
				guiConfigService.removeProperty(key);
			}
		}
		return null;
	}
	
	public ConfigService getGuiConfigService() {
		return guiConfigService;
	}

	public void setGuiConfigService(ConfigService guiConfigService) {
		this.guiConfigService = guiConfigService;
	}
}
