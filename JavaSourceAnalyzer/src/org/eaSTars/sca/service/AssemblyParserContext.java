package org.eaSTars.sca.service;

import java.util.List;

import org.eaSTars.sca.model.JavaAssemblyModel;

import com.github.javaparser.ast.ImportDeclaration;

public class AssemblyParserContext extends ParserContext {

	private JavaAssemblyModel parentJavaAssembly;
	
	private List<ImportDeclaration> imports;
	
	public JavaAssemblyModel getParentJavaAssembly() {
		return parentJavaAssembly;
	}

	public void setParentJavaAssembly(JavaAssemblyModel parentJavaAssembly) {
		this.parentJavaAssembly = parentJavaAssembly;
	}

	public List<ImportDeclaration> getImports() {
		return imports;
	}

	public void setImports(List<ImportDeclaration> imports) {
		this.imports = imports;
	}
}
