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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="java_assembly", uniqueConstraints= {
		@UniqueConstraint(columnNames= {"javasourceproject_id", "parentassembly_id", "name"})
})
public class JavaAssembly implements Serializable {

	private static final long serialVersionUID = -8943672130970729396L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="parentassembly_id", nullable = true)
	private JavaAssembly parent;
	
	@OneToMany(mappedBy="parent", cascade = CascadeType.ALL, orphanRemoval=true)
	private List<JavaAssembly> children = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name="sourcefile_id", nullable = true)
	private SourceFile sourceFile;
	
	@ManyToOne
	@JoinColumn(name="javasourceproject_id", nullable = false)
	private JavaSourceProject javaSourceProject;
	
	@ManyToOne
	@JoinColumn(name="javatype_id", nullable = false)
	private JavaType javaType;
	
	@Column(nullable=false)
	private String name;
	
	@Column(name="aggregatedname", nullable=false)
	private String aggregatedName;
	
	@Column(nullable=false)
	private boolean confirmed = false;

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

	/**
	 * @return the javaSourceProject
	 */
	public JavaSourceProject getJavaSourceProject() {
		return javaSourceProject;
	}

	/**
	 * @param javaSourceProject the javaSourceProject to set
	 */
	public void setJavaSourceProject(JavaSourceProject javaSourceProject) {
		this.javaSourceProject = javaSourceProject;
	}

	/**
	 * @return the aggregatedName
	 */
	public String getAggregatedName() {
		return aggregatedName;
	}

	/**
	 * @param aggregatedName the aggregatedName to set
	 */
	public void setAggregatedName(String aggregatedName) {
		this.aggregatedName = aggregatedName;
	}

	/**
	 * @return the parent
	 */
	public JavaAssembly getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(JavaAssembly parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<JavaAssembly> getChildren() {
		if (children == null) {
			children = new ArrayList<>();
		}
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<JavaAssembly> children) {
		this.children = children;
	}

	/**
	 * @return the confirmed
	 */
	public boolean isConfirmed() {
		return confirmed;
	}

	/**
	 * @param confirmed the confirmed to set
	 */
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}
	
}
