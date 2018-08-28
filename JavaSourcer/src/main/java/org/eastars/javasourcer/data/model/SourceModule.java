package org.eastars.javasourcer.data.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="sourcemodule", uniqueConstraints= {
		@UniqueConstraint(columnNames= {"javasourceproject_id", "name"})
})
public class SourceModule implements Serializable {

	private static final long serialVersionUID = 8131949736860204561L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="javasourceproject_id", nullable = false)
	private JavaSourceProject javaSourceProject;
	
	@Column(unique=true)
	private String name;
	
	@OneToMany(mappedBy="sourceModule", cascade = CascadeType.ALL, orphanRemoval=true)
	private List<SourceFolder> sourceFolders;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public JavaSourceProject getJavaSourceProject() {
		return javaSourceProject;
	}

	public void setJavaSourceProject(JavaSourceProject javaSourceProject) {
		this.javaSourceProject = javaSourceProject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SourceFolder> getSourceFolders() {
		return sourceFolders;
	}

	public void setSourceFolders(List<SourceFolder> sourceFolders) {
		this.sourceFolders = sourceFolders;
	}
	
}
