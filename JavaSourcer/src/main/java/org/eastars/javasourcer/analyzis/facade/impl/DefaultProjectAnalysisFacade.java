package org.eastars.javasourcer.analyzis.facade.impl;

import org.eastars.javasourcer.analyzis.facade.ProjectAnalysisFacade;
import org.eastars.javasourcer.analyzis.service.ModuleContentService;
import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.service.JavaSourcerDataService;
import org.eastars.javasourcer.gui.exception.JavaSourceProjectNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DefaultProjectAnalysisFacade implements ProjectAnalysisFacade {

	private JavaSourcerDataService dataService;
	
	private ModuleContentService moduleContentService;
	
	public DefaultProjectAnalysisFacade(
			JavaSourcerDataService dataService,
			ModuleContentService moduleContentService) {
		this.dataService = dataService;
		this.moduleContentService = moduleContentService;
	}
	
	@Override
	public void analyzeProject(String name) {
		JavaSourceProject javaSourceProject = dataService.getJavaSourceProject(name)
				.orElseThrow(() -> new JavaSourceProjectNotFoundException(name));
		
		moduleContentService.getModulesContent(javaSourceProject);
		
	}
	
}
