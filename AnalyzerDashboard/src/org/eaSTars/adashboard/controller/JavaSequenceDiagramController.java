package org.eaSTars.adashboard.controller;

import org.eaSTars.adashboard.gui.dto.JavaSequenceDiagramView;

public interface JavaSequenceDiagramController {

	public JavaSequenceDiagramView getSequenceView(Integer methodid, boolean ordered);
	
	public JavaSequenceDiagramView updateSequenceView(boolean ordered);
}
