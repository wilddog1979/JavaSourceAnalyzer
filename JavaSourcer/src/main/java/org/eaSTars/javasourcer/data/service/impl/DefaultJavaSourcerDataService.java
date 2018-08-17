package org.eaSTars.javasourcer.data.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eaSTars.javasourcer.data.model.JavaLibrary;
import org.eaSTars.javasourcer.data.model.JavaSourceProject;
import org.eaSTars.javasourcer.data.model.SourceFolder;
import org.eaSTars.javasourcer.data.repository.JavaLibraryRepository;
import org.eaSTars.javasourcer.data.repository.JavaSourceProjectRepository;
import org.eaSTars.javasourcer.data.service.JavaSourcerDataService;
import org.springframework.stereotype.Service;

@Service
public class DefaultJavaSourcerDataService implements JavaSourcerDataService {

	private JavaSourceProjectRepository javaSourceProjectRepo;
	
	private JavaLibraryRepository javaLibraryRepository;
	
	public DefaultJavaSourcerDataService(
			JavaSourceProjectRepository javaSourceProjectRepo,
			JavaLibraryRepository javaLibraryRepository) {
		this.javaSourceProjectRepo = javaSourceProjectRepo;
		this.javaLibraryRepository = javaLibraryRepository;
	}
	
	@Override
	public List<JavaSourceProject> getJavaSourceProjects() {
		return javaSourceProjectRepo.findAllByOrderByCreationDateAsc();
	}

	@Override
	public Optional<JavaSourceProject> getJavaSourceProject(String name) {
		return javaSourceProjectRepo.findByName(name);
	}
	
	@Override
	public void save(JavaSourceProject javaSourceProject) {
		javaSourceProjectRepo.save(javaSourceProject);
	}
	
	@Override
	public boolean updateSourceFolders(JavaSourceProject javaSourceProject, List<String> sourceFolders) {
		List<SourceFolder> toRemove = javaSourceProject.getSourceFolders().stream()
				.filter(sf -> !sourceFolders.contains(sf.getRelativedir()))
				.collect(Collectors.toList());
		
		boolean result = toRemove.size() != 0;
		
		toRemove.stream().forEach(javaSourceProject.getSourceFolders()::remove);
		
		List<SourceFolder> toAdd = sourceFolders.stream()
				.filter(s -> javaSourceProject.getSourceFolders().stream()
						.noneMatch(sf -> sf.getRelativedir().equals(s)))
				.map(s -> {
					SourceFolder sf = new SourceFolder();
					sf.setRelativedir(s);
					return sf;
				}).collect(Collectors.toList());

		result |= toAdd.size() != 0;
		
		toAdd.forEach(sf -> {
			sf.setJavaSourceProject(javaSourceProject);
			javaSourceProject.getSourceFolders().add(sf);
		});
		
		return result;
	}
	
	@Override
	public boolean updateJavaLibraries(JavaSourceProject javaSourceProject, List<String> javaLibraries) {
		List<JavaLibrary> toRemove = javaSourceProject.getJavaLibraries().stream()
				.filter(jl -> !javaLibraries.contains(jl.getName()))
				.collect(Collectors.toList());
		
		boolean result = toRemove.size() != 0;
		
		toRemove.stream().forEach(jl -> {
			javaSourceProject.getJavaLibraries().remove(jl);
			jl.getJavaSourceProjects().remove(javaSourceProject);
		});
		
		List<JavaLibrary> toAdd = javaLibraries.stream()
				.filter(l -> javaSourceProject.getJavaLibraries().stream()
						.noneMatch(jl -> jl.getName().equals(l)))
				.map(l -> javaLibraryRepository.findByName(l).get())
				.collect(Collectors.toList());
		
		result |= toAdd.size() != 0;
		
		toAdd.forEach(jl -> {
			javaSourceProject.getJavaLibraries().add(jl);
			jl.getJavaSourceProjects().add(javaSourceProject);
		});

		return result;
	}

	@Override
	public Stream<String> getLibraryNames() {
		return StreamSupport.stream(javaLibraryRepository.findAll().spliterator(), false)
				.map(JavaLibrary::getName);
	}

}
