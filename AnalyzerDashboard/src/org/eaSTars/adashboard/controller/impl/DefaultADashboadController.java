package org.eaSTars.adashboard.controller.impl;

import java.awt.Dimension;
import java.awt.Point;

import org.eaSTars.adashboard.controller.ADashboardController;
import org.eaSTars.adashboard.gui.ADashboardDialog;
import org.eaSTars.adashboard.gui.FrameClosedListener;
import org.eaSTars.adashboard.gui.MainFrame;
import org.eaSTars.adashboard.gui.MainFrameAdapter;
import org.eaSTars.adashboard.gui.PreferencesDialog;
import org.eaSTars.util.ConfigService;

public class DefaultADashboadController implements ADashboardController {

	private static final String GUI_X = "adashboard.gui.x";
	
	private static final String GUI_Y = "adashboard.gui.y";
	
	private static final String GUI_WIDTH = "adashboard.gui.width";
	
	private static final String GUI_HEIGHT = "adashboard.gui.height";
	
	private static final String GUI_DIVIDER = "adashboard.gui.divider";
	
	private ConfigService configService;
	
	private MainFrame mainframe;
	
	private MainFrameAdapter mainframeAdapter;
	
	private ADashboardDialog aboutDialog;
	
	private PreferencesDialog preferencesDialog;
	
	@Override
	public void buildMenu(boolean extended) {
		mainframe.buildMenu(extended, this);
	}
	
	@Override
	public void start() {
		mainframe.setVisible(true);
	}
	
	@Override
	public void addFrameClosedListener(FrameClosedListener listener) {
		mainframeAdapter.addFrameClosedListener(listener);
	}
	
	@Override
	public void dispatchClosingEvent() {
		mainframe.dispatchClosingEvent();
	}

	@Override
	public void showAbout() {
		aboutDialog.showDialog();
	}
	
	@Override
	public void showPreferences() {
		if (preferencesDialog.showDialog()) {
			//TODO extract settings
		}
	}
	
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
		configService.setProperty(GUI_X, Integer.toString(point.x));
		configService.setProperty(GUI_Y, Integer.toString(point.y));
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
		configService.setProperty(GUI_WIDTH, Integer.toString(dimension.width));
		configService.setProperty(GUI_HEIGHT, Integer.toString(dimension.height));
	}
	
	@Override
	public Integer getDividerLocation() {
		return getIntegerValueFromConfig(GUI_DIVIDER);
	}
	
	@Override
	public void setDividerLocation(Integer dividerlocation) {
		configService.setProperty(GUI_DIVIDER, Integer.toString(dividerlocation));
	}
	
	@Override
	public void saveSettings() {
		configService.saveConfig();
	}
	
	private Integer getIntegerValueFromConfig(String key) {
		String rawvalue = configService.getProperty(key);
		if (rawvalue != null) {
			try {
				return Integer.parseInt(rawvalue);
			} catch (NumberFormatException e) {
				configService.removeProperty(key);
			}
		}
		return null;
	}
	
	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public MainFrame getMainframe() {
		return mainframe;
	}

	public void setMainframe(MainFrame mainframe) {
		this.mainframe = mainframe;
	}

	public MainFrameAdapter getMainframeAdapter() {
		return mainframeAdapter;
	}

	public void setMainframeAdapter(MainFrameAdapter mainframeAdapter) {
		this.mainframeAdapter = mainframeAdapter;
	}

	public ADashboardDialog getAboutDialog() {
		return aboutDialog;
	}

	public void setAboutDialog(ADashboardDialog aboutDialog) {
		this.aboutDialog = aboutDialog;
	}

	public PreferencesDialog getPreferencesDialog() {
		return preferencesDialog;
	}

	public void setPreferencesDialog(PreferencesDialog preferencesDialog) {
		this.preferencesDialog = preferencesDialog;
	}

}
