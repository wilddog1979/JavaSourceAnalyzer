package org.eaSTars.adashboard.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.eaSTars.sca.model.JavaAssemblyModel;

public class TypeDescriptor {

	private JavaAssemblyModel javaAssembly;
	
	private List<TypeDescriptor> arguments = new ArrayList<TypeDescriptor>();

	public JavaAssemblyModel getJavaAssembly() {
		return javaAssembly;
	}

	public void setJavaAssembly(JavaAssemblyModel javaAssembly) {
		this.javaAssembly = javaAssembly;
	}

	public List<TypeDescriptor> getArguments() {
		return arguments;
	} 
}
