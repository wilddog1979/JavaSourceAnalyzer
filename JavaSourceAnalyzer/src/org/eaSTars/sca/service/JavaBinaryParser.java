package org.eaSTars.sca.service;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;

import com.github.javaparser.ast.expr.QualifiedNameExpr;

public interface JavaBinaryParser {

	public JavaAssemblyModel parse(QualifiedNameExpr nameexpr, Class<?> clazz, JavaModuleModel module);
}
