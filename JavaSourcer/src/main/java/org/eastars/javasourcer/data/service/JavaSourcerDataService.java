package org.eastars.javasourcer.data.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.model.SourceFolder;
import org.eastars.javasourcer.gui.dto.LibraryDTO;

public interface JavaSourcerDataService {

	public List<JavaSourceProject> getJavaSourceProjects();

	public Optional<JavaSourceProject> getJavaSourceProject(String name);
	
	public void save(JavaSourceProject javaSourceProject);
	
	public Stream<String> getLibraryNames();
	
	public Stream<String> getPackageNames(String libraryname);
	
	public List<LibraryDTO> getLibraries();

	public void deleteLibrary(String libraryname);

	public void saveLibrary(LibraryDTO library);

	public void save(SourceFolder sf);
	
}
