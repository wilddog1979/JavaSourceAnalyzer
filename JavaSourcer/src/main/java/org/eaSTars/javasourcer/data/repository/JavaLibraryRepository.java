package org.eaSTars.javasourcer.data.repository;

import java.util.Optional;

import org.eaSTars.javasourcer.data.model.JavaLibrary;
import org.springframework.data.repository.CrudRepository;

public interface JavaLibraryRepository extends CrudRepository<JavaLibrary, Long> {

	public Optional<JavaLibrary> findByName(String name);
	
}
