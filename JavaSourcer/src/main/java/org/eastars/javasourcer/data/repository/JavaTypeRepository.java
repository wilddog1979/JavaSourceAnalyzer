package org.eastars.javasourcer.data.repository;

import java.util.Optional;

import org.eastars.javasourcer.data.model.JavaType;
import org.springframework.data.repository.CrudRepository;

public interface JavaTypeRepository extends CrudRepository<JavaType, Long> {

	public Optional<JavaType> findByName(String name);
	
}
