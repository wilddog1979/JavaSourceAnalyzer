package org.eaSTars.javasourcer.data.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="javasourceproject2javalibrary")
public class JavaSourceProject2JavaLibrary implements Serializable {

	private static final long serialVersionUID = -8006300655479644199L;

	@Id
	@ManyToOne
	@JoinColumn(name="javasourceproject_id", nullable = false)
	private JavaSourceProject javaSourceProject;
	
	@Id
	@ManyToOne
	@JoinColumn(name="javalibrary_id", nullable = false)
	private JavaLibrary javaLibrary;

	public JavaSourceProject getJavaSourceProject() {
		return javaSourceProject;
	}

	public void setJavaSourceProject(JavaSourceProject javaSourceProject) {
		this.javaSourceProject = javaSourceProject;
	}

	public JavaLibrary getJavaLibrary() {
		return javaLibrary;
	}

	public void setJavaLibrary(JavaLibrary javaLibrary) {
		this.javaLibrary = javaLibrary;
	}
	
}
