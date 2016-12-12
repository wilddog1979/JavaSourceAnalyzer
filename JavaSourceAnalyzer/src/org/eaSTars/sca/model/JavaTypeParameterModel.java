package org.eaSTars.sca.model;

import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.GenericModel;

@Deployment(table="JavaTypeParameter")
public class JavaTypeParameterModel extends GenericModel {

	@Attribute(column="Name")
	private String name;
	
	@Attribute(column="BoundCount")
	private Integer boundCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getBoundCount() {
		return boundCount;
	}

	public void setBoundCount(Integer boundCount) {
		this.boundCount = boundCount;
	}
}
