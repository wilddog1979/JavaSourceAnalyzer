package org.eaSTars.javasourcer.data.service.impl;

import java.util.List;
import java.util.Optional;

import org.eaSTars.javasourcer.data.model.JavaSourceProject;
import org.eaSTars.javasourcer.data.repository.JavaSourceProjectRepository;
import org.eaSTars.javasourcer.data.service.JavaSourcerDataService;
import org.springframework.stereotype.Service;

@Service
public class DefaultJavaSourcerDataService implements JavaSourcerDataService {

	private JavaSourceProjectRepository javaSourceProjectRepo;
	
	public DefaultJavaSourcerDataService(JavaSourceProjectRepository javaSourceProjectRepo) {
		this.javaSourceProjectRepo = javaSourceProjectRepo;
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
	
}
