package org.eastars.javasourcer.analyzis.service.impl;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eastars.javasourcer.analyzis.service.ProjectAnalyzisService;
import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.service.JavaSourcerDataService;
import org.eastars.javasourcer.fileaccess.service.FileScanner;
import org.eastars.javasourcer.gui.exception.JavaSourceProjectNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DefaultProjectAnalyzisService implements ProjectAnalyzisService {

	private static final Logger LOGGER = LogManager.getLogger(DefaultProjectAnalyzisService.class);
	
	private JavaSourcerDataService dataService;
	
	private FileScanner fileScannerService;
	
	public DefaultProjectAnalyzisService(
			JavaSourcerDataService dataService,
			FileScanner fileScannerService) {
		this.dataService = dataService;
		this.fileScannerService = fileScannerService;
	}
	
	@Override
	public void analyze(String projectName) {
		JavaSourceProject javaSourceProject = dataService.getJavaSourceProject(projectName)
				.orElseThrow(() -> new JavaSourceProjectNotFoundException(projectName));
		
		javaSourceProject.getSourceModules()
		.forEach(m -> m.getSourceFolders()
				.stream().
				flatMap(sf -> fileScannerService.findFiles(new File(javaSourceProject.getBasedir(), sf.getRelativedir()), ".*\\.java$").stream())
				.forEach(f -> {
					LOGGER.info(String.format("[%s] - %s", m.getName(), f.getAbsolutePath()));
				}));
	}
	
}
