package org.eaSTars.adashboard.gui;

import java.awt.event.WindowListener;

public interface MainFrameAdapter extends WindowListener {

	public void addFrameClosingListener(FrameClosingListener frameClosingListener);
	
	public void addFrameClosedListener(FrameClosedListener frameClosedListener);
}
