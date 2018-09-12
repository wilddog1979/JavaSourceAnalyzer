package org.eastars.javasourcer.parser.service;

import java.util.List;
import java.util.Optional;

import org.eastars.javasourcer.data.enumerations.JavaTypes;
import org.eastars.javasourcer.data.model.JavaAssembly;
import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.model.SourceFile;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;

public interface JavaParserMappingService {

	public JavaAssembly mapJavaTypeDeclaration(SourceFile sourcefile, Optional<JavaAssembly> packageAssembly, TypeDeclaration<?> td, JavaTypes javatype);

	public Optional<JavaAssembly> mapPackages(JavaSourceProject javaSourceProject, List<Node> nodes);

}
