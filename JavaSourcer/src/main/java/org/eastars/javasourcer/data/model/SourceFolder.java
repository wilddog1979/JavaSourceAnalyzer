package org.eastars.javasourcer.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="sourcefolder", uniqueConstraints= {
		@UniqueConstraint(columnNames= {"javasourceproject_id", "relativedir"})
})
public class SourceFolder implements Serializable {

	private static final long serialVersionUID = 7465101366628594872L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="javasourceproject_id", nullable = false)
	private JavaSourceProject javaSourceProject;
	
	@Column(nullable=false)
	private String relativedir;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRelativedir() {
		return relativedir;
	}

	public void setRelativedir(String relativedir) {
		this.relativedir = relativedir;
	}

	public JavaSourceProject getJavaSourceProject() {
		return javaSourceProject;
	}

	public void setJavaSourceProject(JavaSourceProject javaSourceProject) {
		this.javaSourceProject = javaSourceProject;
	}
	
}
