package org.eastars.javasourcer.gui.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JRadioButtonMenuItem;

import org.eastars.javasourcer.data.model.JavaLibrary;
import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.model.SourceFolder;
import org.eastars.javasourcer.data.model.SourceModule;
import org.eastars.javasourcer.data.repository.JavaLibraryRepository;
import org.eastars.javasourcer.gui.dto.ModuleDTO;
import org.eastars.javasourcer.gui.dto.ProjectDTO;
import org.eastars.javasourcer.gui.service.ProjectMapperService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DefaultProjectMapperService implements ProjectMapperService {

	private JavaLibraryRepository javaLibraryRepository;
	
	public DefaultProjectMapperService(
			JavaLibraryRepository javaLibraryRepository) {
		this.javaLibraryRepository = javaLibraryRepository;
	}
	
	@Override
	public JRadioButtonMenuItem mapJRadioButtonMenuItem(JavaSourceProject javaSourceProject) {
		return mapJRadioButtonMenuItem(javaSourceProject.getName());
	}
	
	@Override
	public JRadioButtonMenuItem mapJRadioButtonMenuItem(ProjectDTO dto) {
		return mapJRadioButtonMenuItem(dto.getName());
	}
	
	private JRadioButtonMenuItem mapJRadioButtonMenuItem(String name) {
		JRadioButtonMenuItem menuitem = new JRadioButtonMenuItem(name);
		menuitem.setActionCommand(name);
		return menuitem;
	}
	
	@Override
	public void mapJavaSourceProject(JavaSourceProject javaSourceProject, ProjectDTO dto) {
		javaSourceProject.setName(dto.getName());
		
		javaSourceProject.setBasedir(dto.getBasedir());
	}
	
	@Override
	public void mapSourceModules(JavaSourceProject javaSourceProject, List<ModuleDTO> modules) {
		List<SourceModule> sourceModules = javaSourceProject.getSourceModules();

		sourceModules.stream()
		.filter(sm -> modules.stream()
				.noneMatch(m -> sm.getName().equals(m.getOriginalName())))
		.forEach(sourceModules::remove);

		modules.stream()
		.filter(module -> StringUtils.isEmpty(module.getOriginalName()))
		.forEach(module -> {
			SourceModule m = new SourceModule();
			m.setName(module.getName());
			module.getSourceFolders().stream()
			.forEach(sf -> {
				SourceFolder folder = new SourceFolder();
				folder.setRelativedir(sf);
				folder.setSourceModule(m);
				m.getSourceFolders().add(folder);
			});
			sourceModules.add(m);
			m.setJavaSourceProject(javaSourceProject);
		});
	}
	
	@Override
	public void mapSourceModuleFolders(SourceModule module, ModuleDTO sm) {
		module.setName(sm.getName());
		List<SourceFolder> sourceFolders = module.getSourceFolders();

		sourceFolders.stream()
		.filter(sf -> !sm.getSourceFolders().contains(sf.getRelativedir()))
		.forEach(sourceFolders::remove);

		sm.getSourceFolders().stream()
		.filter(f -> sourceFolders.stream()
				.noneMatch(sf -> sf.getRelativedir().equals(f)))
		.forEach(f -> {
			SourceFolder sf = new SourceFolder();
			sf.setRelativedir(f);
			sourceFolders.add(sf);
			sf.setSourceModule(module);
		});
	}
	
	@Override
	public void mapJavaLibraries(JavaSourceProject javaSourceProject, List<String> libraries) {
		List<JavaLibrary> libs = javaSourceProject.getJavaLibraries();

		libs.removeAll(libs.stream()
		.filter(jl -> !libraries.contains(jl.getName()))
		.peek(jl -> jl.getJavaSourceProjects().remove(javaSourceProject))
		.collect(Collectors.toList()));

		libraries.stream()
		.filter(l -> javaSourceProject.getJavaLibraries().stream()
				.noneMatch(jl -> jl.getName().equals(l)))
		.forEach(l -> {
			JavaLibrary jl = javaLibraryRepository.findByName(l).get();
			libs.add(jl);
			jl.getJavaSourceProjects().add(javaSourceProject);
		});
	}
	
}
