package org.eaSTars.sca.model;

import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.ForeignKey;
import org.eaSTars.dblayer.model.GenericModel;

@Deployment(table="JavaThrows")
public class JavaThrowsModel extends GenericModel {

	@Attribute(column="JavaMethodID")
	@ForeignKey(table="JavaMethod", attribute="PK")
	private Integer javaMethodID;
	
	@Attribute(column="JavaTypeID")
	@ForeignKey(table="JavaType", attribute="PK")
	private Integer javaTypeID;

	public Integer getJavaMethodID() {
		return javaMethodID;
	}

	public void setJavaMethodID(Integer javaMethodID) {
		this.javaMethodID = javaMethodID;
	}

	public Integer getJavaTypeID() {
		return javaTypeID;
	}

	public void setJavaTypeID(Integer javaTypeID) {
		this.javaTypeID = javaTypeID;
	}
}
