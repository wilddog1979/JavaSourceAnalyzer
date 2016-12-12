package org.eaSTars.sca.model;

import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.ForeignKey;
import org.eaSTars.dblayer.model.GenericModel;

@Deployment(table="JavaMethodTypeParameter")
public class JavaMethodTypeParameterModel extends GenericModel {

	@Attribute(column="JavaTypeParameterID")
	@ForeignKey(table="JavaTypeParameter", attribute="PK")
	private Integer javaTypeParameterID;
	
	@Attribute(column="JavaMethodID")
	@ForeignKey(table="JavaMethod", attribute="PK")
	private Integer javaMethodID;

	public Integer getJavaTypeParameterID() {
		return javaTypeParameterID;
	}

	public void setJavaTypeParameterID(Integer javaTypeParameterID) {
		this.javaTypeParameterID = javaTypeParameterID;
	}

	public Integer getJavaMethodID() {
		return javaMethodID;
	}

	public void setJavaMethodID(Integer javaMethodID) {
		this.javaMethodID = javaMethodID;
	}
}
