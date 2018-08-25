package org.eastars.javasourcer.data.repository;

import java.util.Optional;

import org.eastars.javasourcer.data.model.JavaLibrary;
import org.springframework.data.repository.CrudRepository;

public interface JavaLibraryRepository extends CrudRepository<JavaLibrary, Long> {

	public Optional<JavaLibrary> findByName(String name);
	
}
