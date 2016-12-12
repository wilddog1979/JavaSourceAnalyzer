package org.eaSTars.sca.model;

import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.ForeignKey;
import org.eaSTars.dblayer.model.GenericModel;

@Deployment(table="JavaMethodParameter")
public class JavaMethodParameterModel extends GenericModel {

	@Attribute(column="JavaMethodID")
	@ForeignKey(table="JavaMethod", attribute="PK")
	private Integer javaMethodID;
	
	@Attribute(column="OrderNumber")
	private Integer orderNumber;
	
	@Attribute(column="Name")
	private String name;
	
	@Attribute(column="JavaTypeID")
	@ForeignKey(table="JavaType", attribute="PK")
	private Integer javaTypeID;

	public Integer getJavaMethodID() {
		return javaMethodID;
	}

	public void setJavaMethodID(Integer javaMethodID) {
		this.javaMethodID = javaMethodID;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getJavaTypeID() {
		return javaTypeID;
	}

	public void setJavaTypeID(Integer javaTypeID) {
		this.javaTypeID = javaTypeID;
	}
}
