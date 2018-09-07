package org.eastars.javasourcer.gui.controller.impl;

import java.awt.Taskbar;
import java.awt.Window;
import java.awt.Taskbar.Feature;
import java.awt.Taskbar.State;
import java.util.Optional;

import org.eastars.javasourcer.gui.controller.ApplicationProgressIndicatorService;

public class DefaultApplicationProgressIndicatorService implements ApplicationProgressIndicatorService {

	private Optional<Taskbar> taskbar;
	
	public DefaultApplicationProgressIndicatorService(Optional<Taskbar> taskbar) {
		this.taskbar = taskbar;
	}
	
	@Override
	public void showProgressValue(int value) {
		taskbar.ifPresent(t -> {
			if (t.isSupported(Feature.PROGRESS_VALUE)) {
				t.setProgressValue(value);
			}
		});
	}
	
	@Override
	public void showProgressValue(Window window, int value) {
		taskbar.ifPresent(t -> {
			if (t.isSupported(Feature.PROGRESS_VALUE_WINDOW)) {
				t.setWindowProgressValue(window, value);
			}
		});
	}
	
	private void showState(Window window, State state) {
		taskbar.ifPresent(t -> {
			if (t.isSupported(Feature.PROGRESS_STATE_WINDOW)) {
				t.setWindowProgressState(window, state);
			}
		});
	}
	
	@Override
	public void showError(Window window) {
		showState(window, State.ERROR);
	}
	
	@Override
	public void showNormal(Window window) {
		showState(window, State.NORMAL);
	}
	
	@Override
	public void hide(Window window) {
		showState(window, State.OFF);
	}
	
	@Override
	public void showPaused(Window window) {
		showState(window, State.PAUSED);
	}
	
	@Override
	public void requestAttention(Window window) {
		taskbar.ifPresent(t -> {
			if (t.isSupported(Feature.USER_ATTENTION_WINDOW)) {
				t.requestWindowUserAttention(window);
			}
		});
	}
	
	private void controlAttentionRequest(boolean enabled, boolean critical) {
		taskbar.ifPresent(t -> {
			if (t.isSupported(Feature.USER_ATTENTION)) {
				t.requestUserAttention(enabled, critical);
			}
		});
	}
	
	@Override
	public void requestAttention(boolean critical) {
		controlAttentionRequest(true, critical);
	}
	
	@Override
	public void dismissAttentionRequest(boolean critical) {
		controlAttentionRequest(false, critical);
	}
	
}
