package org.eaSTars.binprocessor.service;

import org.eaSTars.sca.model.JavaModuleModel;

public interface BinaryDeclarationParser {

	public void parse(Class<?> clazz, JavaModuleModel module);
}
