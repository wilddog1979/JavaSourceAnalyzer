package org.eaSTars.sca.service;

import java.util.List;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;

import com.github.javaparser.ast.ImportDeclaration;

public class AssemblyParserContext {

	private JavaModuleModel javaModule;
	
	private JavaAssemblyModel parentJavaAssembly;
	
	private JavaAssemblyModel javaAssembly;

	private List<ImportDeclaration> imports;
	
	public JavaModuleModel getJavaModule() {
		return javaModule;
	}

	public void setJavaModule(JavaModuleModel javaModule) {
		this.javaModule = javaModule;
	}

	public JavaAssemblyModel getParentJavaAssembly() {
		return parentJavaAssembly;
	}

	public void setParentJavaAssembly(JavaAssemblyModel parentJavaAssembly) {
		this.parentJavaAssembly = parentJavaAssembly;
	}

	public JavaAssemblyModel getJavaAssembly() {
		return javaAssembly;
	}

	public void setJavaAssembly(JavaAssemblyModel javaAssembly) {
		this.javaAssembly = javaAssembly;
	}

	public List<ImportDeclaration> getImports() {
		return imports;
	}

	public void setImports(List<ImportDeclaration> imports) {
		this.imports = imports;
	}
}
