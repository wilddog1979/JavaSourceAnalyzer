package org.eaSTars.adashboard.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eaSTars.adashboard.service.JavaAssemblyService;
import org.eaSTars.adashboard.service.JavaBodyDeclarationService;
import org.eaSTars.adashboard.service.JavaSequenceService;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.model.JavaTypeModel;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;

public class DefaultJavaSequenceService implements JavaSequenceService {

	private static final Logger LOGGER = LogManager.getLogger(DefaultJavaSequenceService.class);
	
	private JavaBodyDeclarationService javaBobyDeclarationService;
	
	private JavaAssemblyService javaAssemblyService;
	
	@Override
	public String generateMethodSequence(Integer methodid) {
		StringBuffer sequencebuffer = new StringBuffer("@startuml\n");
		
		JavaMethodModel javaMethod = javaBobyDeclarationService.getMethod(methodid);
		
		Stack<JavaAssemblyModel> javaAssemblies = new Stack<JavaAssemblyModel>();
		JavaAssemblyModel sourceAccembly = getSourceAssembly(javaMethod, javaAssemblies);
		if (sourceAccembly != null) {
			processSourceFile(javaAssemblies, sourceAccembly, javaMethod, sequencebuffer);
		}
		
		sequencebuffer.append("@enduml\n");
		
		return sequencebuffer.toString();
	}
	
	private void collectDeclarations(List<? extends BodyDeclaration> bodydeclarations, SequenceParserContext ctx) {
		bodydeclarations.stream()
		.filter(bd -> bd instanceof FieldDeclaration)
		.forEach(bd -> {
			FieldDeclaration fd = (FieldDeclaration) bd;
			Type type = fd.getType();
			fd.getVariables().stream()
			.forEach(vd -> ctx.registerDeclaration(vd.getId().getName(), type));
		});
	}
	
	private JavaAssemblyModel getSourceAssembly(JavaMethodModel javaMethod, Stack<JavaAssemblyModel> javaAssemblies) {
		Integer parentid = javaMethod.getParentAssemblyID();
		JavaAssemblyModel javaAssembly = null;
		do {
			javaAssembly = javaAssemblyService.getJavaAssembly(parentid);
			if (javaAssembly != null) {
				parentid = javaAssembly.getParentAssemblyID();
				javaAssemblies.push(javaAssembly);
			}
		} while (javaAssembly != null && javaAssembly.getSubpath() != null && parentid == null);
		
		if (!javaAssemblies.isEmpty() && javaAssemblies.peek().getJavaModuleID() != null) {
			return javaAssemblies.pop();
		} else {
			return null;
		}
	}
	
	private void processSourceFile(Stack<JavaAssemblyModel> javaAssemblies, JavaAssemblyModel javaAssembly, JavaMethodModel javaMethod, StringBuffer sequencebuffer) {
		JavaModuleModel javaModule = javaAssemblyService.getJavaModul(javaAssembly.getJavaModuleID());
		
		try {
			CompilationUnit cu = JavaParser.parse(new File(javaModule.getPath(), javaAssembly.getSubpath()));
			
			SequenceParserContext ctx = new SequenceParserContext();
			ctx.setJavaModule(javaModule);
			ctx.setParentJavaAssembly(javaAssemblyService.getJavaAssemblyByAggregate(cu.getPackage().getPackageName()));
			ctx.setImports(cu.getImports());
			
			List<? extends BodyDeclaration> bodydeclarations = cu.getTypes().stream()
					.filter(td -> td.getName().equals(javaAssembly.getName()))
					.findFirst()
					.map(td -> td.getMembers())
					.orElseGet(() -> new ArrayList<>());

			collectDeclarations(bodydeclarations, ctx);
			
			while (!javaAssemblies.isEmpty()) {
				JavaAssemblyModel jAssembly = javaAssemblies.pop();
				ctx.setParentJavaAssembly(jAssembly);

				bodydeclarations = bodydeclarations.stream()
						.filter(bd -> bd instanceof TypeDeclaration && ((TypeDeclaration)bd).getName().equals(jAssembly.getName()))
						.findFirst()
						.map(bd -> ((TypeDeclaration)bd).getMembers())
						.orElseGet(() -> new ArrayList<>());
				
				ctx.pushNewDeclarationFrame();
				collectDeclarations(bodydeclarations, ctx);
			}
			
			MethodDeclaration methodDeclaration = bodydeclarations.stream()
					//TODO return type and parameter list types should be checked as well
					.filter(bd -> bd instanceof MethodDeclaration && ((MethodDeclaration)bd).getName().equals(javaMethod.getName()))
					.findFirst()
					.map(bd -> (MethodDeclaration)bd)
					.orElseGet(() -> null);
			
			if (methodDeclaration != null) {
				processMethod(ctx, "[", javaAssembly.getName(), methodDeclaration, sequencebuffer);
			}
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void processMethod(SequenceParserContext ctx, String source, String target, MethodDeclaration methodDeclaration, StringBuffer sequencebuffer) {
		LOGGER.debug(() -> methodDeclaration.toStringWithoutComments());
		
		ctx.pushNewDeclarationFrame();
		
		methodDeclaration.getParameters().stream().forEach(p -> {
			ctx.registerDeclaration(p.getId().getName(), p.getType());
		});
		
		sequencebuffer.append(String.format("%s-> %s: %s\n", source, target, methodDeclaration.getName()));
		sequencebuffer.append(String.format("activate %s\n", target));
		
		if (!processStatement(ctx, source, target, methodDeclaration.getBody(), sequencebuffer)) {
			sequencebuffer.append(String.format("%s<-- %s\n", source, target));
		}
		sequencebuffer.append(String.format("deactivate %s\n", target));
		
		ctx.popDeclarationFrame();
	}
	
	private boolean processStatement(SequenceParserContext ctx, String source, String target, Statement statement, StringBuffer sequencebuffer) {
		boolean result = false;

		if (statement instanceof BlockStmt) {
			ctx.pushNewDeclarationFrame();
			result |= ((BlockStmt)statement).getStmts().stream()
					.map(s -> processStatement(ctx, source, target, s, sequencebuffer))
					.reduce(false, (a, b) -> a | b);
			ctx.popDeclarationFrame();
		} else if (statement instanceof DoStmt) {
			DoStmt dostatement = ((DoStmt)statement);
			result |= processStatement(ctx, source, target, dostatement.getBody(), sequencebuffer);
			processExpression(ctx, source, target, dostatement.getCondition(), sequencebuffer);
		} else if (statement instanceof ExpressionStmt) {
			processExpression(ctx, source, target, ((ExpressionStmt) statement).getExpression(), sequencebuffer);
			result = false;
		} else if (statement instanceof IfStmt) {
			IfStmt ifstatement = (IfStmt) statement;
			processExpression(ctx, source, target, ifstatement.getCondition(), sequencebuffer);
			result |= processStatement(ctx, source, target, ifstatement.getThenStmt(), sequencebuffer);
			result |= Optional.ofNullable(ifstatement.getElseStmt()).map(e -> processStatement(ctx, source, target, e, sequencebuffer)).orElseGet(() -> false);
		} else if (statement instanceof ReturnStmt) {
			sequencebuffer.append(String.format(
					"%s<--%s%s\n",
					source,
					target,
					Optional.ofNullable(((ReturnStmt)statement).getExpr())
					.map(e -> {
						processExpression(ctx, source, target, e, sequencebuffer);
						return String.format(":%s", e.toStringWithoutComments().replaceAll("\\.", ".\\\\n"));
					})
					.orElseGet(() -> "")));
			result = true;
		} else if (statement instanceof TryStmt) {
			TryStmt trystatement = (TryStmt) statement;
			result |= processStatement(ctx, source, target, trystatement.getTryBlock(), sequencebuffer);

			result |= trystatement.getCatchs().stream()
					.map(c -> processStatement(ctx, source, target, c.getCatchBlock(), sequencebuffer))
					.reduce(false, (a, b) -> a | b);

			result |= Optional.ofNullable(trystatement.getFinallyBlock())
					.map(c -> {
						ctx.pushNewDeclarationFrame();
						boolean r = c.getStmts().stream()
								.map(s -> processStatement(ctx, source, target, s, sequencebuffer))
								.reduce(false, (a, b) -> a | b);
						ctx.popDeclarationFrame();
						return r;
					})
					.orElseGet(() -> false);
		} else if (statement instanceof WhileStmt) {
			WhileStmt whilestatement = (WhileStmt) statement;
			processExpression(ctx, source, target, whilestatement.getCondition(), sequencebuffer);
			result |= processStatement(ctx, source, target, whilestatement.getBody(), sequencebuffer);
		}

		return result;
	}
	
	private void processExpression(SequenceParserContext ctx, String source, String target, Expression expression, StringBuffer sequencebuffer) {
		LOGGER.debug("Expression %s\n", expression.toStringWithoutComments());
		if (expression instanceof AssignExpr) {
			AssignExpr assignexpresssion = (AssignExpr) expression;
			processExpression(ctx, source, target, assignexpresssion.getTarget(), sequencebuffer);
			processExpression(ctx, source, target, assignexpresssion.getValue(), sequencebuffer);
		} else if (expression instanceof MethodCallExpr) {
			MethodCallExpr methodcall = (MethodCallExpr) expression;
			
			methodcall.getArgs().forEach(a -> processExpression(ctx, source, target, a, sequencebuffer));
			
			if (methodcall.getScope() == null) {
				sequencebuffer.append(String.format("%s -> %s : %s\n", target, target, methodcall.getNameExpr().toStringWithoutComments()));
				sequencebuffer.append(String.format("activate %s\n", target));
				sequencebuffer.append(String.format("deactivate %s\n", target));
			} else if (!methodcall.getScope().toStringWithoutComments().contains("\"")) {
				Type t = ctx.findDeclaration(methodcall.getScope().toStringWithoutComments());
				String ta = null;
				if (t != null) {
					if (t instanceof ReferenceType && ((ReferenceType)t).getType() instanceof ClassOrInterfaceType) {
						TypeDescriptor jtype = resolveTypeDescriptor(ctx, (ReferenceType)t);
						if (jtype != null) {
							jtype = associateType(ctx, jtype);
						}
						if (jtype != null && javaAssemblyService.getJavaModul(jtype.getJavaAssembly().getJavaModuleID()).getIsProject()) {
							ta = jtype.getJavaAssembly().getName();
							sequencebuffer.append(String.format("%s -> %s : %s\n", target, ta, methodcall.getNameExpr().toStringWithoutComments()));
							sequencebuffer.append(String.format("activate %s\n", ta));
							sequencebuffer.append(String.format("deactivate %s\n", ta));
						}
					} else {
						ta = methodcall.getScope().toStringWithoutComments();
						sequencebuffer.append(String.format("%s -> %s : %s\n", target, ta, methodcall.getNameExpr().toStringWithoutComments()));
						sequencebuffer.append(String.format("activate %s\n", ta));
						sequencebuffer.append(String.format("deactivate %s\n", ta));
					}
				}
			}
		} else if (expression instanceof ObjectCreationExpr) {
			((ObjectCreationExpr)expression).getArgs().stream().forEach(a -> processExpression(ctx, source, target, a, sequencebuffer));
		} else if (expression instanceof UnaryExpr) {
			processExpression(ctx, source, target, ((UnaryExpr)expression).getExpr(), sequencebuffer);
		} else if (expression instanceof VariableDeclarationExpr) {
			VariableDeclarationExpr vdecl = (VariableDeclarationExpr) expression;
			vdecl.getVars().stream().forEach(vd -> {
				Optional.ofNullable(vd.getInit()).ifPresent(i -> processExpression(ctx, source, target, i, sequencebuffer));
				ctx.registerDeclaration(vd.getId().getName(), vdecl.getType());
			});
		}
	}
	
	private TypeDescriptor resolveTypeDescriptor(SequenceParserContext ctx, ReferenceType reference) {
		TypeDescriptor result = null;

		Type type = reference.getType();
		if (type instanceof ClassOrInterfaceType) {
			ClassOrInterfaceType classorinterfacetype = (ClassOrInterfaceType) type;

			result = ctx.getImports().stream()
					.map(i -> mapJavaAssemblyModel(i.isAsterisk(), i.getName(), classorinterfacetype.getName()))
					.filter(a -> a != null)
					.findFirst().map(a -> {
						TypeDescriptor td = new TypeDescriptor();

						td.getArguments().addAll(classorinterfacetype.getTypeArgs().stream()
								.filter(t -> t instanceof ReferenceType)
								.map(t -> resolveTypeDescriptor(ctx, (ReferenceType)t))
								.filter(t -> t != null)
								.collect(Collectors.toList()));

						td.setJavaAssembly(a);
						
						JavaTypeModel jta = javaAssemblyService.getJavaType(a, td.getArguments().stream().map(ar -> ar.getJavaType()).collect(Collectors.toList()));
						td.setJavaType(jta);
						
						return td;
					}).orElseGet(() -> null);
		}

		return result;
	}
	
	private JavaAssemblyModel mapJavaAssemblyModel(boolean isAsterisk, NameExpr importname, String name) {
		JavaAssemblyModel result = null;
		
		if (isAsterisk) {
			result = javaAssemblyService.getJavaAssemblyByAggregate(String.format("%s.%s", importname.toStringWithoutComments(), name));
		} else if (importname.getName().equals(name)){
			result = javaAssemblyService.getJavaAssemblyByAggregate(importname.toStringWithoutComments());
		}
		
		return result;
	}
	
	private TypeDescriptor associateType(SequenceParserContext ctx, TypeDescriptor typedescriptor) {
		if (typedescriptor.getJavaAssembly().getJavaObjectTypeID().equals(javaAssemblyService.getInterfaceType().getPK())) {
			// hybris adjustment: Converter types are referring to Populators
			if ("de.hybris.platform.servicelayer.dto.converter.Converter".equals(typedescriptor.getJavaAssembly().getAggregate())) {
				JavaAssemblyModel javaAssembly = javaAssemblyService.getJavaAssemblyByAggregate("de.hybris.platform.converters.Populator"); 
				typedescriptor.setJavaAssembly(javaAssembly);
				typedescriptor.setJavaType(javaAssemblyService.getJavaType(javaAssembly,
						typedescriptor.getArguments().stream().map(a -> a.getJavaType()).collect(Collectors.toList())));
			}
			
			List<JavaAssemblyModel> implementing = javaAssemblyService.getImplementingAssemblies(typedescriptor.getJavaType());
			if (implementing.size() == 1) {
				typedescriptor.setJavaAssembly(implementing.get(0));
			} else {
				LOGGER.warn("Only one class is expected to implement "+typedescriptor.getJavaAssembly().getAggregate()+ " found "+implementing.size());
				typedescriptor = null;
			}
		}
		return typedescriptor;
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
