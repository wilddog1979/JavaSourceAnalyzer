package org.eaSTars.javasourcer.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="javalibrarypackage")
public class JavaLibraryPackage implements Serializable {

	private static final long serialVersionUID = 3186935823131995856L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="javalibrary_id", nullable = false)
	private JavaLibrary javaLibrary;
	
	@Column(unique=true)
	private String packagename;
	
}
