package org.eaSTars.sca.model;

import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.GenericModel;

@Deployment(table="JavaModule")
public class JavaModuleModel extends GenericModel {

	@Attribute(column="Name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
