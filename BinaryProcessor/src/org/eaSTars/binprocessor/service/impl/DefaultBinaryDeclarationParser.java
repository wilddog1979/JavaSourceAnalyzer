package org.eaSTars.binprocessor.service.impl;

import org.eaSTars.binprocessor.service.BinaryDeclarationParser;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;

public class DefaultBinaryDeclarationParser extends AbstractBinaryParser implements BinaryDeclarationParser {

	@Override
	public void parse(Class<?> clazz, JavaModuleModel module) {
		String name = clazz.getName();
		JavaAssemblyModel parentpackage = null;
		int index = name.lastIndexOf('.');
		String classname = name;
		if (index != -1) {
			parentpackage = createJavaPackageStructure(name.substring(0, index), true);
			classname = name.substring(index + 1);
		}
		if (clazz.isInterface()) {
			createOtherStructure(parentpackage, classname, true, module, getJavaInterfaceType());
		} else if (clazz.isEnum()) {
			createOtherStructure(parentpackage, classname, true, module, getJavaEnumType());
		} else if (clazz.isAnnotation()) {
			createOtherStructure(parentpackage, classname, true, module, getJavaAnnotationType());
		} else {
			createOtherStructure(parentpackage, classname, true, module, getJavaClassType());
		}
	}

}
