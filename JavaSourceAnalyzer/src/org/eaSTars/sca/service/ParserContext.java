package org.eaSTars.sca.service;

import java.util.ArrayList;
import java.util.List;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.model.JavaTypeParameterModel;

public abstract class ParserContext {

	private JavaModuleModel javaModule;
	private JavaAssemblyModel javaAssembly;
	private List<JavaTypeParameterModel> javaAssemblyTypeParameters = new ArrayList<JavaTypeParameterModel>();

	public JavaModuleModel getJavaModule() {
		return javaModule;
	}

	public void setJavaModule(JavaModuleModel javaModule) {
		this.javaModule = javaModule;
	}

	public JavaAssemblyModel getJavaAssembly() {
		return javaAssembly;
	}

	public void setJavaAssembly(JavaAssemblyModel javaAssembly) {
		this.javaAssembly = javaAssembly;
	}

	public List<JavaTypeParameterModel> getJavaAssemblyTypeParameters() {
		return javaAssemblyTypeParameters;
	}

}
