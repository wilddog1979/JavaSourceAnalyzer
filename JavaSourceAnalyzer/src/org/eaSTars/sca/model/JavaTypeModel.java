package org.eaSTars.sca.model;

import org.eaSTars.dblayer.model.Attribute;
import org.eaSTars.dblayer.model.Deployment;
import org.eaSTars.dblayer.model.ForeignKey;
import org.eaSTars.dblayer.model.GenericModel;

@Deployment(table="JavaType")
public class JavaTypeModel extends GenericModel {

	@Attribute(column="JavaAssemblyID")
	@ForeignKey(table="JavaAssembly", attribute="PK")
	private Integer javaAssemblyID;

	@Attribute(column="ArgumentCount")
	private Integer argumentCount = 0;
	
	public Integer getJavaAssemblyID() {
		return javaAssemblyID;
	}

	public void setJavaAssemblyID(Integer javaAssemblyID) {
		this.javaAssemblyID = javaAssemblyID;
	}

	public Integer getArgumentCount() {
		return argumentCount;
	}

	public void setArgumentCount(Integer argumentCount) {
		this.argumentCount = argumentCount;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof JavaTypeModel &&
				((JavaTypeModel)obj).getArgumentCount().equals(getArgumentCount()) &&
				((JavaTypeModel)obj).getJavaAssemblyID().equals(getJavaAssemblyID());
	}
}
