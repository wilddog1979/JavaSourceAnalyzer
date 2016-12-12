package org.eaSTars.sca.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;

public interface JavaSourceParser {

	public void process(Map<String, List<File>> modules);
	
	public JavaAssemblyModel processForwardReference(String name, JavaModuleModel module);
}
