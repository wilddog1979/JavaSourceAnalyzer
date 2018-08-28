package org.eastars.javasourcer.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="javalibrary")
public class JavaLibrary implements Serializable {

	private static final long serialVersionUID = -1590033680143653688L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique=true)
	private String name;
	
	@OneToMany(mappedBy="javaLibrary", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<JavaLibraryPackage> javaLibraryPackages;

	@ManyToMany(mappedBy="javaLibraries")
	private List<JavaSourceProject> javaSourceProjects;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<JavaLibraryPackage> getJavaLibraryPackages() {
		if (javaLibraryPackages == null) {
			javaLibraryPackages = new ArrayList<>();
		}
		return javaLibraryPackages;
	}

	public void setJavaLibraryPackages(List<JavaLibraryPackage> javaLibraryPackages) {
		this.javaLibraryPackages = javaLibraryPackages;
	}

	public List<JavaSourceProject> getJavaSourceProjects() {
		if (javaSourceProjects == null) {
			javaSourceProjects = new ArrayList<>();
		}
		return javaSourceProjects;
	}

	public void setJavaSourceProjects(List<JavaSourceProject> javaSourceProjects) {
		this.javaSourceProjects = javaSourceProjects;
	}
	
}
