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
		List<SourceModule> toAdd = modules.stream()
				.filter(module -> StringUtils.isEmpty(module.getOriginalName()) ||
						javaSourceProject.getSourceModules().stream().noneMatch(sm -> sm.getName().equals(module.getName())))
				.map(module -> {
					SourceModule m = new SourceModule();
					m.setName(module.getName());
					m.setSourceFolders(module.getSourceFolders().stream()
							.map(sf -> {
								SourceFolder folder = new SourceFolder();
								folder.setRelativedir(sf);
								folder.setSourceModule(m);
								return folder;
							}).collect(Collectors.toList()));
					return m;
				}).collect(Collectors.toList());
		
		toAdd.forEach(module -> {
			javaSourceProject.getSourceModules().add(module);
			module.setJavaSourceProject(javaSourceProject);
		});
	}
	
	@Override
	public void mapJavaLibraries(JavaSourceProject javaSourceProject, List<String> libraries) {
		List<JavaLibrary> toRemove = javaSourceProject.getJavaLibraries().stream()
				.filter(jl -> !libraries.contains(jl.getName()))
				.collect(Collectors.toList());
		
		toRemove.stream().forEach(jl -> {
			javaSourceProject.getJavaLibraries().remove(jl);
			jl.getJavaSourceProjects().remove(javaSourceProject);
		});
		
		List<JavaLibrary> toAdd = libraries.stream()
				.filter(l -> javaSourceProject.getJavaLibraries().stream()
						.noneMatch(jl -> jl.getName().equals(l)))
				.map(l -> javaLibraryRepository.findByName(l).get())
				.collect(Collectors.toList());
		
		toAdd.forEach(jl -> {
			javaSourceProject.getJavaLibraries().add(jl);
			jl.getJavaSourceProjects().add(javaSourceProject);
		});
	}
	
}
