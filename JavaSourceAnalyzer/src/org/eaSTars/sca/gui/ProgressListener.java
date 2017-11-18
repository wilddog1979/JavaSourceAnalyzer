package org.eaSTars.sca.gui;

public interface ProgressListener {

	public void setOverallCount(int value);
	
	public void setOverallStep(int value);
	
	public void setSubprogressCount(int value);
	
	public void setSubprogressStep(int value);
	
	public void setStatusText(String text);
}
