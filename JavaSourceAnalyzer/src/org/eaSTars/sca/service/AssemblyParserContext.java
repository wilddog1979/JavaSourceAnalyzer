package org.eaSTars.sca.service;

import java.util.ArrayList;
import java.util.List;

import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaTypeParameterModel;

import com.github.javaparser.ast.ImportDeclaration;

public class AssemblyParserContext extends ParserContext {

	private JavaAssemblyModel parentJavaAssembly;
	
	private List<ImportDeclaration> imports;
	
	private List<JavaTypeParameterModel> javaAssemblyTypeParameters = new ArrayList<JavaTypeParameterModel>();
	
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

	public List<JavaTypeParameterModel> getJavaAssemblyTypeParameters() {
		return javaAssemblyTypeParameters;
	}
}
