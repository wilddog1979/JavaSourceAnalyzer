package org.eaSTars.sca.service;

import java.util.List;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;

public interface JavaSourceParser {
	
	public void process(List<ModuleInfo> modules);
	
	public JavaModuleModel getModuleOfReference(String name);
	
	public JavaAssemblyModel processForwardReference(String name);
}
