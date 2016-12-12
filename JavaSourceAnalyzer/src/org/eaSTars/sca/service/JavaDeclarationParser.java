package org.eaSTars.sca.service;

import org.eaSTars.sca.model.JavaAssemblyModel;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;

public interface JavaDeclarationParser {

	public JavaAssemblyModel parse(AssemblyParserContext ctx, ClassOrInterfaceDeclaration coid);
	
	public JavaAssemblyModel parse(AssemblyParserContext ctx, EnumDeclaration ed);
}
