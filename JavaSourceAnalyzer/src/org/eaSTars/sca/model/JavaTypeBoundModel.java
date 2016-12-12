package org.eaSTars.sca.model;

import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.ForeignKey;
import org.eaSTars.dblayer.model.GenericModel;

@Deployment(table="JavaTypeBound")
public class JavaTypeBoundModel extends GenericModel {

	@Attribute(column="JavaTypeParameterID")
	@ForeignKey(table="JavaTypeParameter", attribute="PK")
	private Integer javaTypeParameterID;
	
	@Attribute(column="JavaTypeID")
	@ForeignKey(table="JavaType", attribute="PK")
	private Integer javaTypeID;
	
	@Attribute(column="OrderNumber")
	private Integer orderNumber;

	public Integer getJavaTypeParameterID() {
		return javaTypeParameterID;
	}

	public void setJavaTypeParameterID(Integer javaTypeParameterID) {
		this.javaTypeParameterID = javaTypeParameterID;
	}

	public Integer getJavaTypeID() {
		return javaTypeID;
	}

	public void setJavaTypeID(Integer javaTypeID) {
		this.javaTypeID = javaTypeID;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}
