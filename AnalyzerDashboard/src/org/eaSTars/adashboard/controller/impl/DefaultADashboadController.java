package org.eaSTars.adashboard.controller.impl;

import org.eaSTars.adashboard.controller.ADashboardController;
import org.eaSTars.adashboard.gui.FrameClosedListener;
import org.eaSTars.adashboard.gui.MainFrame;
import org.eaSTars.adashboard.gui.MainFrameAdapter;

public class DefaultADashboadController implements ADashboardController {

	private MainFrame mainframe;
	
	private MainFrameAdapter mainframeAdapter;
	
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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void showPreferences() {
		// TODO Auto-generated method stub
		
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

}
