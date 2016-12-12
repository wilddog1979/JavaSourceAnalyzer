package org.eaSTars.sca.model;

import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.ForeignKey;
import org.eaSTars.dblayer.model.GenericModel;

@Deployment(table="JavaAssemblyTypeParameter")
public class JavaAssemblyTypeParameterModel extends GenericModel {

	@Attribute(column="JavaAssemblyID")
	@ForeignKey(table="JavaAssembly", attribute="PK")
	private Integer javaAssemblyID;
	
	@Attribute(column="JavaTypeParameterID")
	@ForeignKey(table="JavaTypeParameter", attribute="PK")
	private Integer javaTypeParameterID;

	public Integer getJavaAssemblyID() {
		return javaAssemblyID;
	}

	public void setJavaAssemblyID(Integer javaAssemblyID) {
		this.javaAssemblyID = javaAssemblyID;
	}

	public Integer getJavaTypeParameterID() {
		return javaTypeParameterID;
	}

	public void setJavaTypeParameterID(Integer javaTypeParameterID) {
		this.javaTypeParameterID = javaTypeParameterID;
	}
}
