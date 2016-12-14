package org.eaSTars.sca.service;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;

public interface JavaBinaryParser {

	public JavaAssemblyModel parse(Class<?> clazz, JavaModuleModel module);
}
