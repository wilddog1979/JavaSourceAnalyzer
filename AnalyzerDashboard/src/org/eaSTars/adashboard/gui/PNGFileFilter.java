package org.eaSTars.adashboard.gui;

public class PNGFileFilter extends AbstractFileFilter {

	public static final String EXTENSION = ".png";
	
	@Override
	protected String getExtension() {
		return EXTENSION;
	}
	
	@Override
	protected String getFileTypeName() {
		return "PNG file";
	}

}
