package org.eastars.javasourcer.data.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="sourcefile", uniqueConstraints= {
		@UniqueConstraint(columnNames= {"sourcefolder_id", "filename", "creation_date"})
})
@EntityListeners(AuditingEntityListener.class)
public class SourceFile implements Serializable {

	private static final long serialVersionUID = -7455519202035847909L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="sourcefolder_id", nullable = false)
	private SourceFolder sourceFolder;
	
	@CreationTimestamp
	@Column(name="creation_date")
    protected LocalDateTime creationDate;
	
	@Column(nullable=false)
	private String filename;
	
	@OneToMany(mappedBy="sourceFile", cascade = CascadeType.ALL, orphanRemoval=true)
	private List<JavaTypeDeclaration> javaTypeDeclarations = new ArrayList<>();

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
	 * @return the sourceFolder
	 */
	public SourceFolder getSourceFolder() {
		return sourceFolder;
	}

	/**
	 * @param sourceFolder the sourceFolder to set
	 */
	public void setSourceFolder(SourceFolder sourceFolder) {
		this.sourceFolder = sourceFolder;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the creationDate
	 */
	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the javaTypeDeclarations
	 */
	public List<JavaTypeDeclaration> getJavaTypeDeclarations() {
		return javaTypeDeclarations;
	}

	/**
	 * @param javaTypeDeclarations the javaTypeDeclarations to set
	 */
	public void setJavaTypeDeclarations(List<JavaTypeDeclaration> javaTypeDeclarations) {
		this.javaTypeDeclarations = javaTypeDeclarations;
	}
	
}
