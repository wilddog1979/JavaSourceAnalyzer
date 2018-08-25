package org.eastars.javasourcer.data.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="javalibrarypackage", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"javalibrary_id", "packagename"})
})
public class JavaLibraryPackage implements Serializable {

	private static final long serialVersionUID = 3186935823131995856L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="javalibrary_id", nullable = false)
	private JavaLibrary javaLibrary;
	
	private String packagename;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public JavaLibrary getJavaLibrary() {
		return javaLibrary;
	}

	public void setJavaLibrary(JavaLibrary javaLibrary) {
		this.javaLibrary = javaLibrary;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}
	
}
