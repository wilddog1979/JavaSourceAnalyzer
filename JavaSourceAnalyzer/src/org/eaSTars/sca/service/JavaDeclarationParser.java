package org.eaSTars.sca.service;

import java.util.List;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;

public interface JavaDeclarationParser {

	public JavaAssemblyModel parse(JavaAssemblyModel parent, List<ImportDeclaration> imports, ClassOrInterfaceDeclaration coid, JavaModuleModel module);
	
	public JavaAssemblyModel parse(JavaAssemblyModel parent, List<ImportDeclaration> imports, EnumDeclaration ed, JavaModuleModel module);
}
