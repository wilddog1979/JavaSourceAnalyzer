package org.eastars.javasourcer.analyzis.service.impl;

import java.io.File;
import java.net.URI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eastars.javasourcer.analyzis.service.ModuleContentService;
import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.model.SourceFile;
import org.eastars.javasourcer.data.service.JavaSourcerDataService;
import org.eastars.javasourcer.fileaccess.service.FileScanner;
import org.springframework.stereotype.Service;

@Service
public class DefaultModuleContentService implements ModuleContentService {

	private static final Logger LOGGER = LogManager.getLogger(DefaultModuleContentService.class);
	
	private FileScanner fileScannerService;
	
	private JavaSourcerDataService dataService;
	
	public DefaultModuleContentService(
			FileScanner fileScannerService,
			JavaSourcerDataService dataService) {
		this.fileScannerService = fileScannerService;
		this.dataService = dataService;
	}
	
	@Override
	public void getModulesContent(JavaSourceProject javaSourceProject) {
		URI basedir = new File(javaSourceProject.getBasedir()).toURI();
		javaSourceProject.getSourceModules().stream().forEach(m -> 
		m.getSourceFolders().stream().forEach(sf -> {
			LOGGER.info(String.format("[%s]", m.getName()));
			fileScannerService.getModuleContent(new File(javaSourceProject.getBasedir(), sf.getRelativedir()), ".*\\.java$")
			.forEach(f -> {
				SourceFile sourcefile = new SourceFile();
				sourcefile.setFilename(basedir.relativize(new File(f).toURI()).toString());
				sourcefile.setSourceFolder(sf);
				sf.getSourceFiles().add(sourcefile);
				LOGGER.info(String.format("\t- %s", sourcefile.getFilename()));
			});
			dataService.save(sf);
		}));
	}

}
