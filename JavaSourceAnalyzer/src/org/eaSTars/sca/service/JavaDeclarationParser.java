package org.eaSTars.sca.service;

import java.util.List;

import org.eaSTars.sca.model.JavaAssemblyModel;

import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;

public interface JavaDeclarationParser {

	public void processBodyDeclarations(AssemblyParserContext ctx, List<? extends BodyDeclaration> bodydeclarations);
	
	public JavaAssemblyModel parse(AssemblyParserContext ctx, ClassOrInterfaceDeclaration coid);
	
	public JavaAssemblyModel parse(AssemblyParserContext ctx, EnumDeclaration ed);
	
	public JavaAssemblyModel parse(AssemblyParserContext ctx, AnnotationDeclaration ad);
}
