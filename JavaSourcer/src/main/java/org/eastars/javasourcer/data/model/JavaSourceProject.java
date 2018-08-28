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
@Table(name="javasourceproject")
public class JavaSourceProject extends Auditable implements Serializable {

	private static final long serialVersionUID = -298370160335505916L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique=true)
	private String name;

	@Column(nullable=false)
	private String basedir;
	
	@OneToMany(mappedBy="javaSourceProject", cascade = CascadeType.ALL, orphanRemoval=true)
	private List<SourceModule> sourceModules;
	
	@ManyToMany
	private List<JavaLibrary> javaLibraries; 
	
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

	public String getBasedir() {
		return basedir;
	}

	public void setBasedir(String basedir) {
		this.basedir = basedir;
	}

	public List<SourceModule> getSourceModules() {
		if (sourceModules == null) {
			sourceModules = new ArrayList<>();
		}
		return sourceModules;
	}

	public void setSourceModules(List<SourceModule> sourceModules) {
		this.sourceModules = sourceModules;
	}

	public List<JavaLibrary> getJavaLibraries() {
		if (javaLibraries == null) {
			javaLibraries = new ArrayList<>();
		}
		return javaLibraries;
	}

	public void setJavaLibraries(List<JavaLibrary> javaLibraries) {
		this.javaLibraries = javaLibraries;
	}
	
}
