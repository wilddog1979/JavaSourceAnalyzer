package org.eaSTars.sca.service.impl;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.service.JavaBinaryParser;

import com.github.javaparser.ast.expr.QualifiedNameExpr;

public class DefaultJavaBinaryParser extends AbstractJavaParser implements JavaBinaryParser {

	private JavaAssemblyModel parseInterface(QualifiedNameExpr nameexpr, Class<?> clazz, JavaModuleModel module) {
		JavaAssemblyModel result = createOtherStructure(nameexpr, false, module, getJavaInterfaceType(), clazz.getModifiers());
		
		return result;
	}
	
	private JavaAssemblyModel parseEnum(QualifiedNameExpr nameexpr, Class<?> clazz, JavaModuleModel module) {
		JavaAssemblyModel result = createOtherStructure(nameexpr, false, module, getJavaEnumType(), clazz.getModifiers());
		
		return result;
	}
	
	private JavaAssemblyModel parseAnnotation(QualifiedNameExpr nameexpr, Class<?> clazz, JavaModuleModel module) {
		JavaAssemblyModel result = createOtherStructure(nameexpr, false, module, getJavaAnnotationType(), clazz.getModifiers());
		
		return result;
	}
	
	private JavaAssemblyModel parseClass(QualifiedNameExpr nameexpr, Class<?> clazz, JavaModuleModel module) {
		JavaAssemblyModel result = createOtherStructure(nameexpr, false, module, getJavaClassType(), clazz.getModifiers());
		
		return result;
	}
	
	@Override
	public JavaAssemblyModel parse(QualifiedNameExpr nameexpr, Class<?> clazz, JavaModuleModel module) {
		if (clazz.isInterface()) {
			return parseInterface(nameexpr, clazz, module);
		} else if (clazz.isEnum()) {
			return parseEnum(nameexpr, clazz, module);
		} else if (clazz.isAnnotation()) {
			return parseAnnotation(nameexpr, clazz, module);
		} else {
			return parseClass(nameexpr, clazz, module);
		}
	}
}
