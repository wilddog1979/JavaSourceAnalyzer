package org.eastars.javasourcer.analyzis.service;

import org.eastars.javasourcer.data.model.JavaSourceProject;

public interface ModuleContentService {

	public int getModulesContent(JavaSourceProject javaSourceProject);

	public void cleanupModulesContent(JavaSourceProject javaSourceProject);
	
}
