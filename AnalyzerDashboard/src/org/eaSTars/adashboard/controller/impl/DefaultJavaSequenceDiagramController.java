package org.eaSTars.adashboard.controller.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eaSTars.adashboard.controller.JavaSequenceDiagramController;
import org.eaSTars.adashboard.gui.dto.JavaSequenceDiagramView;
import org.eaSTars.adashboard.service.JavaAssemblyService;
import org.eaSTars.adashboard.service.JavaBodyDeclarationService;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.service.AssemblyParserContext;
import org.springframework.core.convert.ConversionService;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

public class DefaultJavaSequenceDiagramController implements JavaSequenceDiagramController {

	private JavaBodyDeclarationService javaBobyDeclarationService;
	
	private JavaAssemblyService javaAssemblyService;
	
	private ConversionService conversionService;
	
	@Override
	public JavaSequenceDiagramView getSequenceView(Integer methodid) {
		StringBuffer sequencebuffer = new StringBuffer("@startuml\n");
		
		JavaMethodModel javaMethod = javaBobyDeclarationService.getMethod(methodid);
		
		Stack<JavaAssemblyModel> javaAssemblies = new Stack<JavaAssemblyModel>();
		Integer parentid = javaMethod.getParentAssemblyID();
		JavaAssemblyModel[] javaAssembly = {null};
		do {
			javaAssembly[0] = javaAssemblyService.getJavaAssembly(parentid);
			if (javaAssembly != null) {
				parentid = javaAssembly[0].getParentAssemblyID();
				javaAssemblies.push(javaAssembly[0]);
			}
		} while (javaAssembly != null && javaAssembly[0].getSubpath() != null && parentid == null);
		
		if (!javaAssemblies.isEmpty() && javaAssemblies.peek().getJavaModuleID() != null) {
			javaAssembly[0] = javaAssemblies.pop();
			JavaModuleModel javaModule = javaAssemblyService.getJavaModul(javaAssembly[0].getJavaModuleID());
			System.out.printf("getSequence: %s%s\n", javaModule.getPath(), javaAssembly[0].getSubpath());
			
			try {
				AssemblyParserContext ctx = new AssemblyParserContext();
				CompilationUnit cu = JavaParser.parse(new File(javaModule.getPath(), javaAssembly[0].getSubpath()));
				
				ctx.setJavaModule(javaModule);
				//ctx.setJavaAssembly(Optional.ofNullable(cu.getPackage()).map(pd -> createJavaPackageStructure(pd.getName(), true)).orElse(null));
				ctx.setImports(cu.getImports());
				
				List<? extends BodyDeclaration> bodydeclarations = cu.getTypes().stream()
						.filter(td -> td.getName().equals(javaAssembly[0].getName()))
						.findFirst()
						.map(td -> td.getMembers())
						.orElseGet(() -> new ArrayList<>());

				while (!javaAssemblies.isEmpty()) {
					String name = javaAssemblies.pop().getName();
					System.out.printf("\t%s\n", name);

					bodydeclarations = bodydeclarations.stream()
							.filter(bd -> bd instanceof TypeDeclaration && ((TypeDeclaration)bd).getName().equals(name))
							.findFirst()
							.map(bd -> ((TypeDeclaration)bd).getMembers())
							.orElseGet(() -> new ArrayList<>());
				}
				sequencebuffer.append(String.format("[-> %s: %s\n", javaAssembly[0].getName(), javaMethod.getName()));
				System.out.printf("\t%s\n", javaMethod.getName());
				sequencebuffer.append(String.format("activate %s\n", javaAssembly[0].getName()));
				
				MethodDeclaration methodDeclaration = bodydeclarations.stream()
						.filter(bd -> bd instanceof MethodDeclaration && ((MethodDeclaration)bd).getName().equals(javaMethod.getName()))
						.findFirst()
						.map(bd -> (MethodDeclaration)bd)
						.orElseGet(() -> null);
				
				System.out.println(methodDeclaration);
				
				sequencebuffer.append(String.format("[<-- %s\n", javaAssembly[0].getName()));
				sequencebuffer.append(String.format("deactivate %s\n", javaAssembly[0].getName()));
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		sequencebuffer.append("@enduml\n");
		
		return conversionService.convert(sequencebuffer.toString(), JavaSequenceDiagramView.class);
	}

	public JavaBodyDeclarationService getJavaBobyDeclarationService() {
		return javaBobyDeclarationService;
	}

	public void setJavaBobyDeclarationService(JavaBodyDeclarationService javaBobyDeclarationService) {
		this.javaBobyDeclarationService = javaBobyDeclarationService;
	}

	public JavaAssemblyService getJavaAssemblyService() {
		return javaAssemblyService;
	}

	public void setJavaAssemblyService(JavaAssemblyService javaAssemblyService) {
		this.javaAssemblyService = javaAssemblyService;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
