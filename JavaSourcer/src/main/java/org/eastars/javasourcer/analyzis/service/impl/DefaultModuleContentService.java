package org.eastars.javasourcer.analyzis.service.impl;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eastars.javasourcer.analyzis.service.ModuleContentService;
import org.eastars.javasourcer.data.model.JavaAssembly;
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
	public int getModulesContent(JavaSourceProject javaSourceProject) {
		return javaSourceProject.getSourceModules().stream()
				.flatMapToInt(m -> m.getSourceFolders().stream().mapToInt(sf -> {
					LOGGER.info(String.format("[%s]", m.getName()));
					File sourcefolderdir = new File(javaSourceProject.getBasedir(), sf.getRelativedir());
					fileScannerService.getModuleContent(sourcefolderdir, ".*\\.java$")
					.stream().map(f -> {
						SourceFile sourcefile = new SourceFile();
						sourcefile.setFilename(sourcefolderdir.toURI().relativize(f.toURI()).toString());
						sourcefile.setSourceFolder(sf);
						LOGGER.info(String.format("\t- %s", sourcefile.getFilename()));
						sf.getSourceFiles().add(dataService.save(sourcefile));
						return dataService.save(sourcefile);
					}).collect(Collectors.toList());
					return sf.getSourceFiles().size();
				})).sum();
	}

	@Override
	public void cleanupModulesContent(JavaSourceProject javaSourceProject) {
		javaSourceProject.getSourceModules()
		.forEach(m -> m.getSourceFolders()
				.forEach(f -> {
					f.getSourceFiles().forEach(sf -> {
						List<JavaAssembly> assemblies = javaSourceProject.getJavaAssemblies().stream()
								.filter(ja -> sf.getJavaAssemblies().stream().anyMatch(ja2 -> ja2.getId().equals(ja.getId())))
								.collect(Collectors.toList());
						javaSourceProject.getJavaAssemblies().removeAll(assemblies);
						sf.getJavaAssemblies().clear();
						dataService.save(sf);
					});
					f.getSourceFiles().clear();
					dataService.save(f);
				}));
		
		dataService.getJavaSourceProject(javaSourceProject.getName())
		.ifPresent(jsp -> {
			jsp.getJavaAssemblies().clear();
			dataService.save(jsp);
		});
	}
	
}
