package org.eaSTars.adashboard.service.dto;

import java.util.ArrayList;
import java.util.List;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaTypeModel;

public class TypeDescriptor {

	private JavaAssemblyModel javaAssembly;
	
	private JavaTypeModel javaType;
	
	private List<TypeDescriptor> arguments = new ArrayList<TypeDescriptor>();

	public JavaAssemblyModel getJavaAssembly() {
		return javaAssembly;
	}

	public void setJavaAssembly(JavaAssemblyModel javaAssembly) {
		this.javaAssembly = javaAssembly;
	}

	public JavaTypeModel getJavaType() {
		return javaType;
	}

	public void setJavaType(JavaTypeModel javaType) {
		this.javaType = javaType;
	}

	public List<TypeDescriptor> getArguments() {
		return arguments;
	} 
}
