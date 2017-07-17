package org.eaSTars.adashboard.gui;

public class PlantUMLFileFilter extends AbstractFileFilter {

	@Override
	protected String getExtension() {
		return ".puml";
	}
	
	@Override
	protected String getFileTypeName() {
		return "PlantUML script";
	}

}
