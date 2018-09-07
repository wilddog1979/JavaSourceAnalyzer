package org.eastars.javasourcer.gui.controller;

import java.awt.Window;

public interface ApplicationProgressIndicatorService {

	public void showProgressValue(int value);
	
	public void showProgressValue(Window window, int value);
	
	public void showError(Window window);
	
	public void showNormal(Window window);
	
	public void hide(Window window);
	
	public void showPaused(Window window);
	
	public void requestAttention(Window window);
	
	public void requestAttention(boolean critical);
	
	public void dismissAttentionRequest(boolean critical);
	
}
