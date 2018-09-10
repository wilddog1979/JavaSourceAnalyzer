package org.eastars.javasourcer.parser.service;

import java.io.File;

import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.model.SourceFile;
import org.eastars.javasourcer.parser.dto.JavaParserContextDTO;

public interface JavaParserService {

	public JavaParserContextDTO createJavaParserContext(JavaSourceProject javaSourceProject);

	public void parse(JavaParserContextDTO javaParserContext, SourceFile sourcefile, File file);

}
