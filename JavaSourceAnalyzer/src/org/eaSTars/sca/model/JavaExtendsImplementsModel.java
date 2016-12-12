package org.eaSTars.sca.model;

import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.ForeignKey;
import org.eaSTars.dblayer.model.GenericModel;

@Deployment(table="JavaExtendsImplements")
public class JavaExtendsImplementsModel extends GenericModel {

	@Attribute(column="ParentJavaAssemblyID")
	@ForeignKey(table="JavaAssembly", attribute="PK")
	private Integer parentJavaAssemblyID;
	
	@Attribute(column="JavaTypeID")
	@ForeignKey(table="JavaType", attribute="PK")
	private Integer javaTypeID;
	
	@Attribute(column="isExtends")
	private Boolean isExtends;

	public Integer getParentJavaAssemblyID() {
		return parentJavaAssemblyID;
	}

	public void setParentJavaAssemblyID(Integer parentJavaAssemblyID) {
		this.parentJavaAssemblyID = parentJavaAssemblyID;
	}

	public Integer getJavaTypeID() {
		return javaTypeID;
	}

	public void setJavaTypeID(Integer javaTypeID) {
		this.javaTypeID = javaTypeID;
	}

	public Boolean getIsExtends() {
		return isExtends;
	}

	public void setIsExtends(Boolean isExtends) {
		this.isExtends = isExtends;
	}
}
