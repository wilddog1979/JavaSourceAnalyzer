package org.eastars.javasourcer.parser.service.impl;

import org.eastars.javasourcer.data.enumerations.JavaTypes;
import org.eastars.javasourcer.data.model.JavaType;
import org.eastars.javasourcer.data.model.JavaTypeDeclaration;
import org.eastars.javasourcer.data.model.SourceFile;
import org.eastars.javasourcer.data.repository.JavaTypeDeclarationRepository;
import org.eastars.javasourcer.data.repository.JavaTypeRepository;
import org.eastars.javasourcer.gui.exception.JavaSourceLookupException;
import org.eastars.javasourcer.parser.service.JavaParserMappingService;
import org.springframework.stereotype.Service;

import com.github.javaparser.ast.body.TypeDeclaration;

@Service
public class DefaultJavaParserMappingService implements JavaParserMappingService {

	private JavaTypeDeclarationRepository javaTypeDeclarationRepository;
	
	private JavaTypeRepository javaTypeRepository;
	
	public DefaultJavaParserMappingService(
			JavaTypeDeclarationRepository javaTypeDeclarationRepository,
			JavaTypeRepository javaTypeRepository) {
		this.javaTypeDeclarationRepository = javaTypeDeclarationRepository;
		this.javaTypeRepository = javaTypeRepository;
	}
	
	@Override
	public JavaTypeDeclaration mapJavaTypeDeclaration(SourceFile sourcefile, TypeDeclaration<?> td, JavaTypes javatype) {
		JavaTypeDeclaration javaTypeDeclaration = new JavaTypeDeclaration();
		javaTypeDeclaration.setJavaType(javaTypeRepository.findByName(javatype.name())
				.orElseThrow(() -> new JavaSourceLookupException(JavaType.class.getName(), javatype.name())));
		javaTypeDeclaration.setSourceFile(sourcefile);
		javaTypeDeclaration.setName(td.getName().asString());
		sourcefile.getJavaTypeDeclarations().add(javaTypeDeclarationRepository.save(javaTypeDeclaration));
		return javaTypeDeclaration;
	}
	
}
