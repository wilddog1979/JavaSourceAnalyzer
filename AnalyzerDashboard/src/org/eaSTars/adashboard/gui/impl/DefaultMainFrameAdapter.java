package org.eaSTars.adashboard.gui.impl;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import org.eaSTars.adashboard.gui.FrameClosedListener;
import org.eaSTars.adashboard.gui.MainFrameAdapter;

public class DefaultMainFrameAdapter extends WindowAdapter implements MainFrameAdapter {

	private List<FrameClosedListener> frameClosedListeners = new ArrayList<FrameClosedListener>();
	
	@Override
	public void addFrameClosedListener(FrameClosedListener frameClosedListener) {
		frameClosedListeners.add(frameClosedListener);
	}
	
	@Override
	public void windowClosed(WindowEvent e) {
		frameClosedListeners.forEach(l -> l.frameClosed(e.getWindow()));
	}
}
