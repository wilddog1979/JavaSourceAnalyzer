package org.eaSTars.sca.dao;

import java.util.ArrayList;
import java.util.List;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaTypeModel;

public class TypeArgumentEntry {

	private JavaAssemblyModel type;
	
	private List<JavaTypeModel> arguments = new ArrayList<JavaTypeModel>();

	public JavaAssemblyModel getType() {
		return type;
	}

	public void setType(JavaAssemblyModel type) {
		this.type = type;
	}

	public List<JavaTypeModel> getArguments() {
		return arguments;
	}
}
