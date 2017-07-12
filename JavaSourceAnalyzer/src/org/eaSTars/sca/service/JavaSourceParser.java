package org.eaSTars.sca.service;

import java.util.List;

import org.eaSTars.sca.model.JavaAssemblyModel;

public interface JavaSourceParser {
	
	public void process(List<ModuleInfo> modules);
	
	public JavaAssemblyModel processForwardReference(String name);
}
