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
		JavaAssembly javaAssembly = new JavaAssembly();
		javaAssembly.setJavaType(resolveJavaType(javatype));
		javaAssembly.setJavaSourceProject(sourcefile.getSourceFolder().getSourceModule().getJavaSourceProject());
		javaAssembly.setSourceFile(sourcefile);
		javaAssembly.setName(td.getName().asString());
		javaAssembly.setAggregatedName(javaAssembly.getAggregatedName());
		
		if (packageAssembly.isPresent()) {
			javaAssembly.setParent(packageAssembly.get());
			javaAssembly.setAggregatedName(String.format("%s.%s", packageAssembly.get().getAggregatedName(), javaAssembly.getName()));
		}
		
		javaAssembly = javaAssemblyRepository.save(javaAssembly);
		sourcefile.getJavaAssemblies().add(javaAssembly);
		return javaAssembly;
	}
	
	private JavaType resolveJavaType(JavaTypes javatype) {
		return javaTypeRepository.findByName(javatype.name())
				.orElseThrow(() -> new JavaSourceLookupException(JavaType.class.getName(), javatype.name()));
	}
	
	@Override
	public Optional<JavaAssembly> mapPackages(JavaSourceProject javaSourceProject, List<Node> nodes) {
		return nodes.stream().findFirst().map(n -> {
			Optional<JavaAssembly> parentassembly = mapPackages(javaSourceProject, n.getChildNodes());
			
			return javaSourceProject.getJavaAssemblies().stream()
			.filter(j -> j.getName().equals(((Name)n).getIdentifier()) && parentassembly.map(p -> j.getParent() != null && p.getId() == j.getParent().getId()).orElseGet(() -> j.getParent() == null))
			.findFirst()
			.orElseGet(() -> {
				JavaAssembly ja = new JavaAssembly();
				
				ja.setJavaSourceProject(javaSourceProject);
				ja.setJavaType(resolveJavaType(JavaTypes.PACKAGE));
				ja.setName(((Name)n).getIdentifier());
				
				ja.setAggregatedName(ja.getName());
				if (parentassembly.isPresent()) {
					ja.setParent(parentassembly.get());
					ja.setAggregatedName(String.format("%s.%s", parentassembly.get().getAggregatedName(), ja.getName()));
				}
				
				ja = javaAssemblyRepository.save(ja);
				javaSourceProject.getJavaAssemblies().add(ja);
				
				if (parentassembly.isPresent()) {
					parentassembly.get().getChildren().add(ja);
				}
				return ja;
			});
		});
	}
	
}
