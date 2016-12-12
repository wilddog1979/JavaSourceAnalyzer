package org.eaSTars.sca.model;

import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.ForeignKey;
import org.eaSTars.dblayer.model.GenericModel;

@Deployment(table="JavaAssembly")
public class JavaAssemblyModel extends GenericModel {

	@Attribute(column="JavaModuleID", nullable=true)
	@ForeignKey(table="JavaModule", attribute="PK")
	private Integer javaModuleID;
	
	@Attribute(column="ParentAssemblyID", nullable=true)
	@ForeignKey(table="JavaAssembly", attribute="PK")
	private Integer parentAssemblyID;
	
	@Attribute(column="Modifiers", nullable=true)
	private Integer modifiers;
	
	@Attribute(column="Name")
	private String name;

	@Attribute(column="JavaObjectTypeID")
	@ForeignKey(table="JavaObjectType", attribute="PK")
	private Integer javaObjectTypeID;
	
	@Attribute(column="Aggregate")
	private String aggregate;
	
	@Attribute(column="Confirmed")
	private Boolean confirmed;
	
	public Integer getJavaModuleID() {
		return javaModuleID;
	}

	public void setJavaModuleID(Integer javaModuleID) {
		this.javaModuleID = javaModuleID;
	}

	public Integer getParentAssemblyID() {
		return parentAssemblyID;
	}

	public void setParentAssemblyID(Integer parentAssemblyID) {
		this.parentAssemblyID = parentAssemblyID;
	}

	public Integer getModifiers() {
		return modifiers;
	}

	public void setModifiers(Integer modifiers) {
		this.modifiers = modifiers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getJavaObjectTypeID() {
		return javaObjectTypeID;
	}

	public void setJavaObjectTypeID(Integer javaObjectyTypeID) {
		this.javaObjectTypeID = javaObjectyTypeID;
	}

	public String getAggregate() {
		return aggregate;
	}

	public void setAggregate(String aggregate) {
		this.aggregate = aggregate;
	}

	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}
}
