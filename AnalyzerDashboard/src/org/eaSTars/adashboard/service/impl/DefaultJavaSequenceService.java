package org.eaSTars.adashboard.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import org.eaSTars.adashboard.service.JavaAssemblyService;
import org.eaSTars.adashboard.service.JavaBodyDeclarationService;
import org.eaSTars.adashboard.service.JavaSequenceService;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.service.AssemblyParserContext;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

public class DefaultJavaSequenceService implements JavaSequenceService {

	private JavaBodyDeclarationService javaBobyDeclarationService;
	
	private JavaAssemblyService javaAssemblyService;
	
	@Override
	public String generateMethodSequence(Integer methodid) {
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
				
				System.out.printf("\t%s\n", javaMethod.getName());
				
				MethodDeclaration methodDeclaration = bodydeclarations.stream()
						.filter(bd -> bd instanceof MethodDeclaration && ((MethodDeclaration)bd).getName().equals(javaMethod.getName()))
						.findFirst()
						.map(bd -> (MethodDeclaration)bd)
						.orElseGet(() -> null);
				
				if (methodDeclaration != null) {
					System.out.println(methodDeclaration.toStringWithoutComments());
					
					sequencebuffer.append(String.format("[-> %s: %s\n", javaAssembly[0].getName(), javaMethod.getName()));
					sequencebuffer.append(String.format("activate %s\n", javaAssembly[0].getName()));
					
					if (!processStatement(ctx, "[", javaAssembly[0].getName(), methodDeclaration.getBody(), sequencebuffer)) {
						sequencebuffer.append(String.format("[<-- %s\n", javaAssembly[0].getName()));
					}
					sequencebuffer.append(String.format("deactivate %s\n", javaAssembly[0].getName()));
				}
				
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		sequencebuffer.append("@enduml\n");
		
		return sequencebuffer.toString();
	}
	
	private boolean processStatement(AssemblyParserContext ctx, String source, String target, Statement statement, StringBuffer sequencebuffer) {
		boolean result = false;

		if (statement instanceof BlockStmt) {
			result |= ((BlockStmt)statement).getStmts().stream()
					.map(s -> processStatement(ctx, source, target, s, sequencebuffer))
					.reduce(false, (a, b) -> a | b);
		} else if (statement instanceof ReturnStmt) {
			sequencebuffer.append(String.format(
					"%s<--%s%s\n",
					source,
					target,
					Optional.ofNullable(((ReturnStmt)statement).getExpr())
					.map(e -> {
						processExpression(ctx, source, target, e, sequencebuffer);
						return e;
					})
					.map(e -> String.format(":%s", e.toStringWithoutComments()))
					.orElseGet(() -> "")));
			result = true;
		} else if (statement instanceof IfStmt) {
			IfStmt ifstatement = (IfStmt) statement;
			processExpression(ctx, source, target, ifstatement.getCondition(), sequencebuffer);
			result |= processStatement(ctx, source, target, ifstatement.getThenStmt(), sequencebuffer);
			
			result |= Optional.ofNullable(ifstatement.getElseStmt()).map(e -> processStatement(ctx, source, target, e, sequencebuffer)).orElseGet(() -> false);
		}

		return result;
	}
	
	private void processExpression(AssemblyParserContext ctx, String source, String target, Expression expression, StringBuffer sequencebuffer) {
		System.out.println(expression.toStringWithoutComments());
		if (expression instanceof UnaryExpr) {
			processExpression(ctx, source, target, ((UnaryExpr)expression).getExpr(), sequencebuffer);
		} else if (expression instanceof MethodCallExpr) {
			MethodCallExpr methodcall = (MethodCallExpr) expression;
			sequencebuffer.append(String.format("%s -> %s : %s\n", target, methodcall.getScope().toStringWithoutComments(), methodcall.getNameExpr().toStringWithoutComments()));
		}
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

}
