package org.eastars.javasourcer.parser.service;

import org.eastars.javasourcer.data.enumerations.JavaTypes;
import org.eastars.javasourcer.data.model.JavaTypeDeclaration;
import org.eastars.javasourcer.data.model.SourceFile;

import com.github.javaparser.ast.body.TypeDeclaration;

public interface JavaParserMappingService {

	public JavaTypeDeclaration mapJavaTypeDeclaration(SourceFile sourcefile, TypeDeclaration<?> td, JavaTypes javatype);

}
