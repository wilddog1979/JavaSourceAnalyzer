package org.eaSTars.sca.model;

import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.ForeignKey;
import org.eaSTars.dblayer.model.GenericModel;

@Deployment(table="JavaTypeArgument")
public class JavaTypeArgumentModel extends GenericModel {

	@Attribute(column="ParentJavaTypeID")
	@ForeignKey(table="JavaType", attribute="PK")
	private Integer parentJavaTypeID;
	
	@Attribute(column="JavaTypeID")
	@ForeignKey(table="JavaType", attribute="PK")
	private Integer javaTypeID;
	
	@Attribute(column="OrderNumber")
	private Integer orderNumber;

	public Integer getParentJavaTypeID() {
		return parentJavaTypeID;
	}

	public void setParentJavaTypeID(Integer parentJavaTypeID) {
		this.parentJavaTypeID = parentJavaTypeID;
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
