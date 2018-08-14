package org.eaSTars.javasourcer.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	
	@OneToMany(mappedBy="javaSourceProject")
	private List<SourceFolder> sourceFolders;
	
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

	public List<SourceFolder> getSourceFolders() {
		if (sourceFolders == null) {
			sourceFolders = new ArrayList<>();
		}
		return sourceFolders;
	}

	public void setSourceFolders(List<SourceFolder> sourceFolders) {
		this.sourceFolders = sourceFolders;
	}
	
}
