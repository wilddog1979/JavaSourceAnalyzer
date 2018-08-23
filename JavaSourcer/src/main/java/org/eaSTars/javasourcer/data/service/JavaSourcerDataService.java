package org.eaSTars.javasourcer.data.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eaSTars.javasourcer.data.model.JavaSourceProject;
import org.eaSTars.javasourcer.gui.dto.LibraryDTO;

public interface JavaSourcerDataService {

	public List<JavaSourceProject> getJavaSourceProjects();

	public Optional<JavaSourceProject> getJavaSourceProject(String name);
	
	public void save(JavaSourceProject javaSourceProject);
	
	public boolean updateSourceFolders(JavaSourceProject javaSourceProject, List<String> sourceFolders);

	public boolean updateJavaLibraries(JavaSourceProject javaSourceProject, List<String> javaLibraries);
	
	public Stream<String> getLibraryNames();
	
	public Stream<String> getPackageNames(String libraryname);
	
	public List<LibraryDTO> getLibraries();

	public void deleteLibrary(String libraryname);

	public void saveLibrary(LibraryDTO library);
	
}
