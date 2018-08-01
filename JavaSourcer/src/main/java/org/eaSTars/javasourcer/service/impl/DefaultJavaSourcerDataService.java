package org.eaSTars.javasourcer.service.impl;

import java.util.List;

import org.eaSTars.javasourcer.model.JavaSourceProject;
import org.eaSTars.javasourcer.repository.JavaSourceProjectRepository;
import org.eaSTars.javasourcer.service.JavaSourcerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DefaultJavaSourcerDataService implements JavaSourcerDataService {

	@Autowired
	private JavaSourceProjectRepository javaSourceProjectRepo;
	
	@Override
	public List<JavaSourceProject> getJavaSourceProjects() {
		return javaSourceProjectRepo.findAllByOrderByCreationDateAsc();
	}

}
