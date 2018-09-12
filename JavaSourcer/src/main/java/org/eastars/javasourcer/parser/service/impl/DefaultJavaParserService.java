package org.eastars.javasourcer.parser.service.impl;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eastars.javasourcer.data.enumerations.JavaTypes;
import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.model.JavaAssembly;
import org.eastars.javasourcer.data.model.SourceFile;
import org.eastars.javasourcer.parser.dto.JavaParserContextDTO;
import org.eastars.javasourcer.parser.service.JavaParserMappingService;
import org.eastars.javasourcer.parser.service.JavaParserService;
import org.springframework.stereotype.Service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

@Service
public class DefaultJavaParserService implements JavaParserService {
	
	private static final Logger LOGGER = LogManager.getLogger(DefaultJavaParserService.class);
	
	private JavaParserMappingService javaParserMappingService;
	
	public DefaultJavaParserService(
			JavaParserMappingService javaParserMappingService) {
		this.javaParserMappingService = javaParserMappingService;
	}
	
	@Override
	public JavaParserContextDTO createJavaParserContext(JavaSourceProject javaSourceProject) {
		JavaParserContextDTO javaParserContext = new JavaParserContextDTO();
		
		CombinedTypeSolver typeSolver = new CombinedTypeSolver();
		typeSolver.add(new ReflectionTypeSolver(true));
		javaSourceProject.getSourceModules()
		.forEach(sourcemodule -> sourcemodule.getSourceFolders().stream()
				.forEach(sourcefolder -> typeSolver.add(new JavaParserTypeSolver(new File(javaSourceProject.getBasedir(), sourcefolder.getRelativedir())))));
		
		javaParserContext.setJavaParserFacade(JavaParserFacade.get(typeSolver));
		
		return javaParserContext;
	}
	
	@Override
	public void parse(JavaParserContextDTO javaParserContext, SourceFile sourcefile, File file) {
		try {
			CompilationUnit compilationUnit = JavaParser.parse(file);
			compilationUnit.getTypes().stream().forEach(td -> {
				LOGGER.debug(String.format("%s - %s", td.getName(), td.getClass().getName()));
				
				JavaAssembly javaTypeDeclaration = td.toAnnotationDeclaration()
						.map(a -> javaParserMappingService.mapJavaTypeDeclaration(sourcefile, a, JavaTypes.ANNOTATION))
						.orElseGet(() -> td.toClassOrInterfaceDeclaration().map(ci -> javaParserMappingService.mapJavaTypeDeclaration(sourcefile, ci, ci.isInterface() ? JavaTypes.INTERFACE : JavaTypes.CLASS))
								.orElseGet(() -> td.toClassOrInterfaceDeclaration().map(e -> javaParserMappingService.mapJavaTypeDeclaration(sourcefile, e, JavaTypes.ENUMERATION))
										.orElseGet(() -> null)));
			});
		} catch (FileNotFoundException e) {
			LOGGER.error("Error while parsing " + file.getName(), e);
		}
	}
	
}
