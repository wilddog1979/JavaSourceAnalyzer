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
@Table(name="java_type_declaration", uniqueConstraints= {
		@UniqueConstraint(columnNames= {"sourcefile_id", "name"})
})
public class JavaTypeDeclaration implements Serializable {

	private static final long serialVersionUID = -8943672130970729396L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="sourcefile_id", nullable = false)
	private SourceFile sourceFile;
	
	@ManyToOne
	@JoinColumn(name="javatype_id", nullable = false)
	private JavaType javaType;
	
	@Column(nullable=false)
	private String name;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the sourceFile
	 */
	public SourceFile getSourceFile() {
		return sourceFile;
	}

	/**
	 * @param sourceFile the sourceFile to set
	 */
	public void setSourceFile(SourceFile sourceFile) {
		this.sourceFile = sourceFile;
	}

	/**
	 * @return the javaType
	 */
	public JavaType getJavaType() {
		return javaType;
	}

	/**
	 * @param javaType the javaType to set
	 */
	public void setJavaType(JavaType javaType) {
		this.javaType = javaType;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
