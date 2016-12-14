package org.eaSTars.sca.service;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;

public abstract class ParserContext {

	private JavaModuleModel javaModule;
	private JavaAssemblyModel javaAssembly;

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

}
