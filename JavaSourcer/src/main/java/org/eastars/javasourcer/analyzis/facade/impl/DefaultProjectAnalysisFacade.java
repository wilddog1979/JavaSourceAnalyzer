package org.eastars.javasourcer.analyzis.facade.impl;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eastars.javasourcer.analyzis.facade.ProjectAnalysisFacade;
import org.eastars.javasourcer.analyzis.service.ModuleContentService;
import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.service.JavaSourcerDataService;
import org.eastars.javasourcer.gui.exception.JavaSourceProjectNotFoundException;
import org.eastars.javasourcer.parser.dto.JavaParserContextDTO;
import org.eastars.javasourcer.parser.service.JavaParserService;
import org.springframework.stereotype.Service;

@Service
public class DefaultProjectAnalysisFacade implements ProjectAnalysisFacade {

	private static final Logger LOGGER = LogManager.getLogger(DefaultProjectAnalysisFacade.class);
	
	private JavaSourcerDataService dataService;
	
	private ModuleContentService moduleContentService;
	
	private JavaParserService javaParserService;
	
	public DefaultProjectAnalysisFacade(
			JavaSourcerDataService dataService,
			ModuleContentService moduleContentService,
			JavaParserService javaParserService) {
		this.dataService = dataService;
		this.moduleContentService = moduleContentService;
		this.javaParserService = javaParserService;
	}
	
	@Override
	public void analyzeProject(String name) {
		JavaSourceProject javaSourceProject = dataService.getJavaSourceProject(name)
				.orElseThrow(() -> new JavaSourceProjectNotFoundException(name));
		
		moduleContentService.cleanupModulesContent(javaSourceProject);
		
		int numberoffiles = moduleContentService.getModulesContent(javaSourceProject);
		LOGGER.info(String.format("Number of files: %d", numberoffiles));
		
		JavaParserContextDTO javaParserContext = javaParserService.createJavaParserContext(javaSourceProject);
		
		javaSourceProject.getSourceModules()
		.forEach(sourcemodule -> sourcemodule.getSourceFolders().stream()
				.forEach(sourcefolder -> sourcefolder.getSourceFiles()
						.forEach(sourcefile -> javaParserService.parse(
								javaParserContext,
								sourcefile,
								new File(new File(javaSourceProject.getBasedir(), sourcefolder.getRelativedir()), sourcefile.getFilename())))));
	}
	
}
