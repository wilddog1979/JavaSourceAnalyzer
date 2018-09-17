package org.eastars.javasourcer.parser.service.impl;

import java.util.List;
import java.util.Optional;

import org.eastars.javasourcer.data.enumerations.JavaTypes;
import org.eastars.javasourcer.data.model.JavaAssembly;
import org.eastars.javasourcer.data.model.JavaSourceProject;
import org.eastars.javasourcer.data.model.JavaType;
import org.eastars.javasourcer.data.model.SourceFile;
import org.eastars.javasourcer.data.repository.JavaAssemblyRepository;
import org.eastars.javasourcer.data.repository.JavaTypeRepository;
import org.eastars.javasourcer.gui.exception.JavaSourceLookupException;
import org.eastars.javasourcer.parser.service.JavaParserMappingService;
import org.springframework.stereotype.Service;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Name;

@Service
public class DefaultJavaParserMappingService implements JavaParserMappingService {

	private JavaAssemblyRepository javaAssemblyRepository;
	
	private JavaTypeRepository javaTypeRepository;
	
	public DefaultJavaParserMappingService(
			JavaAssemblyRepository javaAssemblyRepository,
			JavaTypeRepository javaTypeRepository) {
		this.javaAssemblyRepository = javaAssemblyRepository;
		this.javaTypeRepository = javaTypeRepository;
	}
	
	@Override
	public JavaAssembly mapJavaTypeDeclaration(SourceFile sourcefile, Optional<JavaAssembly> packageAssembly, TypeDeclaration<?> td, JavaTypes javatype) {
		return mapJavaAssembly(javatype, sourcefile.getSourceFolder().getSourceModule().getJavaSourceProject(), sourcefile, packageAssembly, td.getName().asString());
	}
	
	private JavaAssembly mapJavaAssembly(JavaTypes javatype, JavaSourceProject javaSourceProject, SourceFile sourcefile, Optional<JavaAssembly> parentAssembly,
			String name) {
		JavaAssembly javaAssembly = new JavaAssembly();
		javaAssembly.setJavaType(resolveJavaType(javatype));
		javaAssembly.setJavaSourceProject(javaSourceProject);
		javaAssembly.setSourceFile(sourcefile);
		javaAssembly.setName(name);
		javaAssembly.setAggregatedName(name);
		
		if (parentAssembly.isPresent()) {
			javaAssembly.setParent(parentAssembly.get());
			javaAssembly.setAggregatedName(String.format("%s.%s", parentAssembly.get().getAggregatedName(), javaAssembly.getName()));
		}
		
		javaAssembly = javaAssemblyRepository.save(javaAssembly);
		if (sourcefile != null) {
			sourcefile.getJavaAssemblies().add(javaAssembly);
		}
		javaSourceProject.getJavaAssemblies().add(javaAssembly);
		return javaAssembly;
	}

	private JavaType resolveJavaType(JavaTypes javatype) {
		return javaTypeRepository.findByName(javatype.name())
				.orElseThrow(() -> new JavaSourceLookupException(JavaType.class.getName(), javatype.name()));
	}
	
	@Override
	public Optional<JavaAssembly> mapPackages(JavaSourceProject javaSourceProject, List<Node> nodes) {
		return nodes.stream().findFirst().map(n -> javaSourceProject.getJavaAssemblies().stream()
				.filter(ja -> ((ja.getParent() == null && n.getChildNodes().isEmpty()) ||
						(ja.getParent() != null && !n.getChildNodes().isEmpty() && ja.getParent().getAggregatedName().equals(n.getChildNodes().get(0).toString()))) &&
						ja.getName().equals(((Name)n).getIdentifier()))
				.findFirst()
				.orElseGet(() -> mapJavaAssembly(
						JavaTypes.PACKAGE,
						javaSourceProject,
						null,
						n.getChildNodes().isEmpty() ? Optional.empty() : mapPackages(javaSourceProject, n.getChildNodes()),
								((Name)n).getIdentifier())));
	}

}
