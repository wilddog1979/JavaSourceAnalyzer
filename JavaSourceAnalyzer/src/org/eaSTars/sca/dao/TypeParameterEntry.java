package org.eaSTars.sca.dao;

import java.util.ArrayList;
import java.util.List;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaTypeParameterModel;

public class TypeParameterEntry {

	private JavaTypeParameterModel typeParameter;
	
	private List<JavaAssemblyModel> bounds = new ArrayList<JavaAssemblyModel>();

	public JavaTypeParameterModel getTypeParameter() {
		return typeParameter;
	}

	public void setTypeParameter(JavaTypeParameterModel typeParameter) {
		this.typeParameter = typeParameter;
	}

	public List<JavaAssemblyModel> getBounds() {
		return bounds;
	}
}
