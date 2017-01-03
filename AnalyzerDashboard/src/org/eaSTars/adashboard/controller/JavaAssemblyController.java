package org.eaSTars.adashboard.controller;

import java.util.List;

import org.eaSTars.adashboard.gui.dto.ADashboardObjectView;
import org.eaSTars.adashboard.gui.dto.JavaAssemblyFullView;

public interface JavaAssemblyController {

	public List<ADashboardObjectView> getChildAssemblies(Integer parentId);
	
	public List<ADashboardObjectView> getAssemblyMethods(Integer parentId);
	
	public JavaAssemblyFullView getAssemblyFullView(Integer id);
}
