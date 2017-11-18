package org.eaSTars.adashboard.gui.impl;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import org.eaSTars.adashboard.gui.FrameClosedListener;
import org.eaSTars.adashboard.gui.FrameClosingListener;
import org.eaSTars.adashboard.gui.MainFrameAdapter;

public class DefaultMainFrameAdapter extends WindowAdapter implements MainFrameAdapter {

	private List<FrameClosingListener> frameClosingListeners = new ArrayList<FrameClosingListener>();
	
	private List<FrameClosedListener> frameClosedListeners = new ArrayList<FrameClosedListener>();
	
	@Override
	public void addFrameClosingListener(FrameClosingListener frameClosingListener) {
		frameClosingListeners.add(frameClosingListener);
	}
	
	@Override
	public void addFrameClosedListener(FrameClosedListener frameClosedListener) {
		frameClosedListeners.add(frameClosedListener);
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		frameClosingListeners.forEach(l -> l.frameClosing(e.getWindow()));
	}
	
	@Override
	public void windowClosed(WindowEvent e) {
		frameClosedListeners.forEach(l -> l.frameClosed(e.getWindow()));
	}
}
