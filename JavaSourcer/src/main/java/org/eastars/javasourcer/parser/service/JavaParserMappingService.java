package org.eastars.javasourcer.parser.service;

import org.eastars.javasourcer.data.enumerations.JavaTypes;
import org.eastars.javasourcer.data.model.JavaAssembly;
import org.eastars.javasourcer.data.model.SourceFile;

import com.github.javaparser.ast.body.TypeDeclaration;

public interface JavaParserMappingService {

	public JavaAssembly mapJavaTypeDeclaration(SourceFile sourcefile, TypeDeclaration<?> td, JavaTypes javatype);

}
