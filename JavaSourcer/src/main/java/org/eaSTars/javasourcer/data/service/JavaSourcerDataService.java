package org.eaSTars.javasourcer.data.service;

import java.util.List;
import java.util.Optional;

import org.eaSTars.javasourcer.data.model.JavaSourceProject;

public interface JavaSourcerDataService {

	public List<JavaSourceProject> getJavaSourceProjects();

	public Optional<JavaSourceProject> getJavaSourceProject(String name);
	
	public void save(JavaSourceProject javaSourceProject);
	
}
