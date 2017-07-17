package org.eaSTars.adashboard.gui;

public class PlantUMLFileFilter extends AbstractFileFilter {

	public static final String EXTENSION = ".puml";
	
	@Override
	protected String getExtension() {
		return ".puml";
	}
	
	@Override
	protected String getFileTypeName() {
		return "PlantUML script";
	}

}
