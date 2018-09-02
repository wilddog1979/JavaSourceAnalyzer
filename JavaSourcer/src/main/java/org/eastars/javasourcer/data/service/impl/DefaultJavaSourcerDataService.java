package org.eastars.javasourcer.data.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eastars.javasourcer.data.model.JavaLibrary;
import org.eastars.javasourcer.data.model.JavaLibraryPackage;
import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.repository.JavaLibraryRepository;
import org.eastars.javasourcer.data.repository.JavaSourceProjectRepository;
import org.eastars.javasourcer.data.service.JavaSourcerDataService;
import org.eastars.javasourcer.gui.dto.LibraryDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
	public Stream<String> getLibraryNames() {
		return StreamSupport.stream(javaLibraryRepository.findAll().spliterator(), false)
				.map(JavaLibrary::getName);
	}

	@Override
	public Stream<String> getPackageNames(String libraryname) {
		return javaLibraryRepository
				.findByName(libraryname)
				.map(jl -> jl.getJavaLibraryPackages().stream()
						.map(JavaLibraryPackage::getPackagename))
				.orElseGet(Stream::empty);
	}
	
	@Override
	public List<LibraryDTO> getLibraries() {
		return StreamSupport.stream(javaLibraryRepository.findAll().spliterator(), false)
				.map(jl -> {
					LibraryDTO dto = new LibraryDTO();
					dto.setOriginalName(jl.getName());
					dto.setName(jl.getName());
					dto.setPackages(jl.getJavaLibraryPackages().stream()
							.map(JavaLibraryPackage::getPackagename)
							.collect(Collectors.toList()));
					return dto;
				}).collect(Collectors.toList());
	}
	
	@Override
	public void deleteLibrary(String libraryname) {
		javaLibraryRepository.findByName(libraryname).ifPresent(javaLibraryRepository::delete);
	}
	
	@Override
	public void saveLibrary(LibraryDTO library) {
		boolean dirty = false;
		
		JavaLibrary lib;
		if (StringUtils.isEmpty(library.getOriginalName())) {
			lib = new JavaLibrary();
			lib.setName(library.getName());
			lib.setJavaLibraryPackages(library.getPackages().stream().map(p -> {
				JavaLibraryPackage pack = new JavaLibraryPackage();
				pack.setPackagename(p);
				pack.setJavaLibrary(lib);
				return pack;
			}).collect(Collectors.toList()));
			dirty = true;
		} else {
			
			lib = javaLibraryRepository.findByName(library.getOriginalName()).orElseGet(() -> null);
			if (!library.getOriginalName().equals(library.getName())) {
				lib.setName(library.getName());
				dirty = true;
			}
			
			List<JavaLibraryPackage> toAdd = library.getPackages().stream()
			.filter(p -> lib.getJavaLibraryPackages().stream().noneMatch(lp -> p.equals(lp.getPackagename())))
			.map(p -> {
				JavaLibraryPackage lp = new JavaLibraryPackage();
				lp.setPackagename(p);
				return lp;
			})
			.collect(Collectors.toList());
			
			dirty |= !toAdd.isEmpty();
			
			toAdd.forEach(lp -> {
				lib.getJavaLibraryPackages().add(lp);
				lp.setJavaLibrary(lib);
			});
			
			List<JavaLibraryPackage> toRemove = lib.getJavaLibraryPackages().stream()
			.filter(lp -> !library.getPackages().contains(lp.getPackagename()))
			.collect(Collectors.toList());
			
			dirty |= !toRemove.isEmpty();
			
			toRemove.forEach(lib.getJavaLibraryPackages()::remove);
		}
		
		if (dirty) {
			javaLibraryRepository.save(lib);
		}
	}
	
}
