package org.eaSTars.adashboard.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eaSTars.adashboard.service.JavaAssemblyService;
import org.eaSTars.adashboard.service.JavaBodyDeclarationService;
import org.eaSTars.adashboard.service.JavaSequenceService;
import org.eaSTars.adashboard.service.JavaTypeService;
import org.eaSTars.adashboard.service.dto.JavaSequenceScript;
import org.eaSTars.adashboard.service.dto.TypeDescriptor;
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
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;

public class DefaultJavaSequenceService implements JavaSequenceService {

	private static final Logger LOGGER = LogManager.getLogger(DefaultJavaSequenceService.class);

	private JavaBodyDeclarationService javaBobyDeclarationService;

	private JavaAssemblyService javaAssemblyService;

	private JavaTypeService javaTypeService;
	
	@Override
	public JavaSequenceScript generateMethodSequence(Integer methodid) {
		JavaSequenceScript sequencebuffer = new JavaSequenceScript();

		JavaMethodModel javaMethod = javaBobyDeclarationService.getMethod(methodid);

		processMethod(javaMethod, "[", sequencebuffer);

		return sequencebuffer;
	}

	private void processMethod(JavaMethodModel javaMethod, String source, JavaSequenceScript sequencebuffer) {
		Stack<JavaAssemblyModel> javaAssemblies = getSourceAssembly(javaMethod.getParentAssemblyID());
		if (!javaAssemblies.isEmpty()) {
			String embedded = IntStream.range(0, javaAssemblies.size())
					.mapToObj(c -> javaAssemblies.get(javaAssemblies.size() - c - 1).getName())
					.collect(Collectors.joining("."));

			JavaAssemblyModel sourceAccembly = javaAssemblies.pop();
			SequenceParserContext ctx = new SequenceParserContext();
			ctx.setJavaAssembly(sourceAccembly);

			List<? extends BodyDeclaration> bodydeclarations = processSourceFile(ctx, javaAssemblies, sourceAccembly);

			MethodDeclaration methodDeclaration = bodydeclarations.stream()
					//TODO return type and parameter list types should be checked as well
					.filter(bd -> bd instanceof MethodDeclaration &&
							((MethodDeclaration)bd).getName().equals(javaMethod.getName()) &&
							((MethodDeclaration)bd).getParameters().size() == javaMethod.getParameterCount())
					.findFirst()
					.map(bd -> (MethodDeclaration)bd)
					.orElseGet(() -> null);

			if (methodDeclaration != null) {
				if (!sequencebuffer.pushToCallStack(javaMethod.getPK())) {
					processMethod(ctx, source, sequencebuffer.addParticipant(embedded, sourceAccembly), methodDeclaration, sequencebuffer);
					sequencebuffer.popFromCallStack();
				} else {
					LOGGER.warn("Avoiding recursion: " + embedded + "." + javaMethod.getName());
				}
			}
		}
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

	private Stack<JavaAssemblyModel> getSourceAssembly(Integer assemblyID) {
		Stack<JavaAssemblyModel> javaAssemblies = new Stack<JavaAssemblyModel>();

		JavaAssemblyModel javaAssembly = null;
		do {
			javaAssembly = javaAssemblyService.getJavaAssembly(assemblyID);
			if (javaAssembly != null &&
					javaAssemblyService.getPackageType() != null &&
					!javaAssembly.getJavaObjectTypeID().equals(javaAssemblyService.getPackageType().getPK())) {
				assemblyID = javaAssembly.getParentAssemblyID();
				javaAssemblies.push(javaAssembly);
			} else {
				break;
			}
		} while (assemblyID != null);

		return javaAssemblies;
	}

	private List<? extends BodyDeclaration> processSourceFile(SequenceParserContext ctx, Stack<JavaAssemblyModel> javaAssemblies, JavaAssemblyModel javaAssembly) {
		JavaModuleModel javaModule = javaAssemblyService.getJavaModul(javaAssembly.getJavaModuleID());

		if (javaModule != null && javaModule.getPath() != null && javaAssembly != null && javaAssembly.getSubpath() != null) {
			try {
				CompilationUnit cu = JavaParser.parse(new File(javaModule.getPath(), javaAssembly.getSubpath()));

				ctx.setJavaModule(javaModule);
				ctx.setParentJavaAssembly(javaAssemblyService.getJavaAssemblyByAggregate(cu.getPackage().getPackageName()));
				ctx.setImports(cu.getImports());
				ctx.setJavaAssembly(ctx.getParentJavaAssembly());

				List<? extends BodyDeclaration> bodydeclarations = cu.getTypes().stream()
						.filter(td -> td.getName().equals(javaAssembly.getName()))
						.findFirst()
						.map(td -> extractBodyDeclarations(ctx, td))
						.orElseGet(() -> new ArrayList<>());

				collectDeclarations(bodydeclarations, ctx);

				while (!javaAssemblies.isEmpty()) {
					JavaAssemblyModel jAssembly = javaAssemblies.pop();
					ctx.setParentJavaAssembly(jAssembly);

					bodydeclarations = bodydeclarations.stream()
							.filter(bd -> bd instanceof TypeDeclaration && ((TypeDeclaration)bd).getName().equals(jAssembly.getName()))
							.findFirst()
							.map(bd -> extractBodyDeclarations(ctx, (TypeDeclaration) bd))
							.orElseGet(() -> new ArrayList<>());

					ctx.pushNewDeclarationFrame();
					collectDeclarations(bodydeclarations, ctx);
				}

				return bodydeclarations;
			} catch (ParseException | IOException e) {
				LOGGER.error("Error while processing source code", e);
			}
		}
		return Collections.emptyList();
	}

	private List<BodyDeclaration> extractBodyDeclarations(SequenceParserContext ctx, TypeDeclaration td) {
		ctx.setJavaAssembly(javaAssemblyService.getAssembly(ctx.getJavaAssembly().getPK(), td.getName()));
		return td.getMembers();
	}

	private void processMethod(SequenceParserContext ctx, String source, String target, MethodDeclaration methodDeclaration, JavaSequenceScript sequencebuffer) {
		LOGGER.debug(() -> methodDeclaration.toStringWithoutComments());

		ctx.pushNewDeclarationFrame();

		methodDeclaration.getParameters().stream().forEach(p -> {
			ctx.registerDeclaration(p.getId().getName(), p.getType());
		});

		sequencebuffer.appendContent(String.format("%s-> %s: %s\n", source, target, methodDeclaration.getName()));
		sequencebuffer.appendContent(String.format("activate %s\n", target));

		if (!processStatement(ctx, source, target, methodDeclaration.getBody(), sequencebuffer)) {
			sequencebuffer.appendContent(String.format("%s<-- %s\n", source, target));
		}
		sequencebuffer.appendContent(String.format("deactivate %s\n", target));

		ctx.popDeclarationFrame();
	}

	private boolean processStatement(SequenceParserContext ctx, String source, String target, Statement statement, JavaSequenceScript sequencebuffer) {
		boolean result = false;

		if (statement instanceof BlockStmt) {
			ctx.pushNewDeclarationFrame();
			result |= ((BlockStmt)statement).getStmts().stream()
					.map(s -> processStatement(ctx, source, target, s, sequencebuffer))
					.reduce(false, (a, b) -> a | b);
			ctx.popDeclarationFrame();
		} else if (statement instanceof BreakStmt) {
			result = false;
		} else if (statement instanceof ContinueStmt) {
			result = false;
		} else if (statement instanceof DoStmt) {
			DoStmt dostatement = ((DoStmt)statement);
			result |= processStatement(ctx, source, target, dostatement.getBody(), sequencebuffer);
			processExpression(ctx, source, target, dostatement.getCondition(), sequencebuffer);
		} else if (statement instanceof ExpressionStmt) {
			processExpression(ctx, source, target, ((ExpressionStmt) statement).getExpression(), sequencebuffer);
			result = false;
		} else if (statement instanceof ForeachStmt) {
			ForeachStmt foreachstatement = (ForeachStmt) statement;

			processExpression(ctx, source, target, foreachstatement.getIterable(), sequencebuffer);
			processExpression(ctx, source, target, foreachstatement.getVariable(), sequencebuffer);

			result = processStatement(ctx, source, target, foreachstatement.getBody(), sequencebuffer);
		} else if (statement instanceof ForStmt) {
			ForStmt forstatement = (ForStmt) statement;

			forstatement.getInit().forEach(i -> processExpression(ctx, source, target, i, sequencebuffer));
			processExpression(ctx, source, target, forstatement.getCompare(), sequencebuffer);
			forstatement.getUpdate().forEach(u -> processExpression(ctx, source, target, u, sequencebuffer));

			result = processStatement(ctx, source, target, forstatement.getBody(), sequencebuffer);
		} else if (statement instanceof IfStmt) {
			IfStmt ifstatement = (IfStmt) statement;
			processExpression(ctx, source, target, ifstatement.getCondition(), sequencebuffer);
			result |= processStatement(ctx, source, target, ifstatement.getThenStmt(), sequencebuffer);
			result |= Optional.ofNullable(ifstatement.getElseStmt()).map(e -> processStatement(ctx, source, target, e, sequencebuffer)).orElseGet(() -> false);
		} else if (statement instanceof ReturnStmt) {
			sequencebuffer.appendContent(String.format(
					"%s<--%s%s\n",
					source,
					target,
					Optional.ofNullable(((ReturnStmt)statement).getExpr())
					.map(e -> {
						processExpression(ctx, source, target, e, sequencebuffer);
						//TODO add option to turn on/off return labels
						//return String.format(":%s", e.toStringWithoutComments().replaceAll("\\.", ".\\\\n"));
						return "";
					})
					.orElseGet(() -> "")));
			result = true;
		} else if (statement instanceof ThrowStmt) {
			processExpression(ctx, source, target, ((ThrowStmt)statement).getExpr(), sequencebuffer);
			result = false;
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
		} else if (statement != null) {
			LOGGER.warn("Missing statement processor: " + statement.getClass().getSimpleName());
		}

		return result;
	}

	private TypeDescriptor processExpression(SequenceParserContext ctx, String source, String target, Expression expression, JavaSequenceScript sequencebuffer) {
		LOGGER.debug("Expression %s\n", expression.toStringWithoutComments());
		TypeDescriptor expressiontype = null;
		if (expression instanceof ArrayAccessExpr) {
			ArrayAccessExpr arrayaccessexpression = (ArrayAccessExpr) expression;
			expressiontype = processExpression(ctx, source, target, arrayaccessexpression.getName(), sequencebuffer);
			processExpression(ctx, source, target, arrayaccessexpression.getIndex(), sequencebuffer);
		} else if (expression instanceof ArrayCreationExpr) {
			ArrayCreationExpr arraycreationexpression = (ArrayCreationExpr) expression;

			Optional.ofNullable(arraycreationexpression.getInitializer()).ifPresent(i -> processExpression(ctx, source, target, arraycreationexpression.getInitializer(), sequencebuffer));
			//TODO define type???
		} else if (expression instanceof ArrayInitializerExpr) {
			((ArrayInitializerExpr)expression).getValues().stream().map(i -> processExpression(ctx, source, target, i, sequencebuffer));
			//TODO define type???
		} else if (expression instanceof AssignExpr) {
			AssignExpr assignexpresssion = (AssignExpr) expression;
			expressiontype = processExpression(ctx, source, target, assignexpresssion.getTarget(), sequencebuffer);
			processExpression(ctx, source, target, assignexpresssion.getValue(), sequencebuffer);
		} else if (expression instanceof BinaryExpr) {
			BinaryExpr binaryexpression = (BinaryExpr) expression;
			processExpression(ctx, source, target, binaryexpression.getLeft(), sequencebuffer);
			processExpression(ctx, source, target, binaryexpression.getRight(), sequencebuffer);
			expressiontype = createTypeDescriptor(javaAssemblyService.getJavaAssemblyByAggregate("java.lang.Boolean"), Collections.emptyList());
		} else if (expression instanceof BooleanLiteralExpr) {
			expressiontype = createTypeDescriptor(javaAssemblyService.getJavaAssemblyByAggregate("java.lang.Boolean"), Collections.emptyList());
		} else if (expression instanceof CastExpr) {
			CastExpr castexpression = (CastExpr) expression;
			processExpression(ctx, source, target, castexpression.getExpr(), sequencebuffer);
			expressiontype = resolveTypeDescriptor(ctx, castexpression.getType());
		} else if (expression instanceof ConditionalExpr) {
			ConditionalExpr conditionalexpression = (ConditionalExpr) expression;
			processExpression(ctx, source, target, conditionalexpression.getCondition(), sequencebuffer);
			TypeDescriptor t1 = processExpression(ctx, source, target, conditionalexpression.getThenExpr(), sequencebuffer);
			TypeDescriptor t2 = processExpression(ctx, source, target, conditionalexpression.getElseExpr(), sequencebuffer);
			if (t1 != null) {
				expressiontype = t1;
			} else {
				expressiontype = t2;
			}
		} else if (expression instanceof ClassExpr) {
			TypeDescriptor td = resolveTypeDescriptor(ctx, ((ClassExpr)expression).getType());
			expressiontype = createTypeDescriptor(javaAssemblyService.getJavaAssemblyByAggregate("java.lang.Class"), Arrays.asList(td.getJavaType()));
		} else if (expression instanceof EnclosedExpr) {
			expressiontype = processExpression(ctx, source, target, ((EnclosedExpr)expression).getInner(), sequencebuffer);
		} else if (expression instanceof FieldAccessExpr) {
			// maybe not important(???)
		} else if (expression instanceof InstanceOfExpr) {
			InstanceOfExpr instanceofexpression = (InstanceOfExpr) expression;
			processExpression(ctx, source, target, instanceofexpression.getExpr(), sequencebuffer);
			resolveTypeDescriptor(ctx, instanceofexpression.getType());
			expressiontype = createTypeDescriptor(javaAssemblyService.getJavaAssemblyByAggregate("java.lang.Boolean"), Collections.emptyList());
		} else if (expression instanceof MethodCallExpr) {
			MethodCallExpr methodcall = (MethodCallExpr) expression;

			List<TypeDescriptor> parametertypes = methodcall.getArgs().stream().map(a -> processExpression(ctx, source, target, a, sequencebuffer)).collect(Collectors.toList());

			if (methodcall.getScope() == null) {
				expressiontype = methodCall(ctx, ctx.getJavaAssembly(), methodcall.getNameExpr().toStringWithoutComments(), parametertypes, target, sequencebuffer);
			} else if (!methodcall.getScope().toStringWithoutComments().contains("\"")) {
				TypeDescriptor td = processExpression(ctx, source, target, methodcall.getScope(), sequencebuffer);
				TypeDescriptor jtype = associateType(ctx, td);

				JavaModuleModel jam = jtype == null ? null : javaAssemblyService.getJavaModul(jtype.getJavaAssembly().getJavaModuleID());

				if (jtype != null && jam.getIsProject() && !"Bootstrap".equals(jam.getName())) {
					expressiontype = methodCall(ctx, jtype.getJavaAssembly(), methodcall.getNameExpr().toStringWithoutComments(), parametertypes, target, sequencebuffer);
				}
			} else if (methodcall != null) {
				processExpression(ctx, source, target, methodcall.getScope(), sequencebuffer);
			}
		} else if (expression instanceof NameExpr) {
			Type type = ctx.findDeclaration(expression.toStringWithoutComments());
			if (type == null) {
				// static reference
				expressiontype = resolveTypeFromImport(ctx, expression.toStringWithoutComments(), Collections.emptyList());
			} else {
				// variable
				expressiontype = resolveTypeDescriptor(ctx, type);
			}
		} else if (expression instanceof NullLiteralExpr) {
			//TODO figure out what to do
		} else if (expression instanceof ObjectCreationExpr) {
			ObjectCreationExpr objectcreation = (ObjectCreationExpr) expression;
			objectcreation.getArgs().stream().forEach(a -> processExpression(ctx, source, target, a, sequencebuffer));
			expressiontype = resolveTypeDescriptor(ctx, objectcreation.getType());
		} else if (expression instanceof StringLiteralExpr) {
			expressiontype = createTypeDescriptor(javaAssemblyService.getJavaAssemblyByAggregate("java.lang.String"), Collections.emptyList());
		} else if (expression instanceof ThisExpr) {
			expressiontype = new TypeDescriptor();
			expressiontype.setJavaAssembly(ctx.getJavaAssembly());
		} else if (expression instanceof UnaryExpr) {
			processExpression(ctx, source, target, ((UnaryExpr)expression).getExpr(), sequencebuffer);
			expressiontype = createTypeDescriptor(javaAssemblyService.getJavaAssemblyByAggregate("java.lang.Boolean"), Collections.emptyList());
		} else if (expression instanceof VariableDeclarationExpr) {
			VariableDeclarationExpr vdecl = (VariableDeclarationExpr) expression;
			vdecl.getVars().stream().forEach(vd -> {
				Optional.ofNullable(vd.getInit()).ifPresent(i -> processExpression(ctx, source, target, i, sequencebuffer));
				ctx.registerDeclaration(vd.getId().getName(), vdecl.getType());
			});
			expressiontype = resolveTypeDescriptor(ctx, vdecl.getType());
		} else if (expression != null) {
			LOGGER.warn("Missing expression processor: " + expression.getClass().getSimpleName());
		}
		return expressiontype;
	}

	private TypeDescriptor methodCall(SequenceParserContext ctx, JavaAssemblyModel javaAssembly, String methodname, List<TypeDescriptor> parametertypes, String target, JavaSequenceScript sequencebuffer) {
		List<JavaMethodModel> methods = javaBobyDeclarationService.getMethods(javaAssembly, methodname, parametertypes);

		if (methods.size() != 1) {
			LOGGER.warn("Only one matching method expected: "+javaAssembly.getName() + "." + methodname + " (" + methods.size() + ")");
		}

		JavaMethodModel method = null;     
		if (!methods.isEmpty()) {
			method = methods.get(0);
		}

		if (method != null) {
			processMethod(method, target, sequencebuffer);
			return resolveTypeDescriptor(method.getJavaTypeID());
		}
		return null;
	}

	private TypeDescriptor resolveTypeDescriptor(int typeid) {
		TypeDescriptor result = new TypeDescriptor();

		JavaTypeModel javaType = javaTypeService.getJavaType(typeid);
		result.setJavaType(javaType);
		result.setJavaAssembly(javaAssemblyService.getJavaAssembly(javaType.getJavaAssemblyID()));     
		result.getArguments().addAll(javaTypeService.getTypeArguments(javaType).stream()
				.map(jt -> resolveTypeDescriptor(jt.getPK())).collect(Collectors.toList()));

		return result;
	}

	private TypeDescriptor resolveTypeDescriptor(SequenceParserContext ctx, Type type) {
		TypeDescriptor result = null;

		if (type instanceof ClassOrInterfaceType) {
			ClassOrInterfaceType classorinterfacetype = (ClassOrInterfaceType) type;

			result = resolveTypeFromImport(ctx, classorinterfacetype.getName(), classorinterfacetype.getTypeArgs());
		} else if (type instanceof PrimitiveType) {
			result = createTypeDescriptor(javaAssemblyService.getJavaAssemblyByAggregate(String.format("java.lang.%s", ((PrimitiveType)type).getType().name())), Collections.emptyList());
		} else if (type instanceof ReferenceType) {
			result = resolveTypeDescriptor(ctx, ((ReferenceType)type).getType());
		} else if (type instanceof VoidType) {
			result = createTypeDescriptor(javaAssemblyService.getJavaAssemblyByAggregate("void"), Collections.emptyList());
		} else if (type != null){
			LOGGER.warn("Missing type processor: " + type.getClass().getSimpleName());
		}

		return result;
	}

	private TypeDescriptor resolveTypeFromImport(SequenceParserContext ctx, String name, List<Type> typeargs) {
		return ctx.getImports().stream()
				.map(i -> mapJavaAssemblyModel(i.isAsterisk(), i.getName(), name))
				.filter(a -> a != null)
				.findFirst().map(a -> {
					TypeDescriptor td = new TypeDescriptor();

					td.getArguments().addAll(typeargs.stream()
							.filter(t -> t instanceof ReferenceType)
							.map(t -> resolveTypeDescriptor(ctx, t))
							.filter(t -> t != null)
							.collect(Collectors.toList()));

					td.setJavaAssembly(a);

					JavaTypeModel jta = javaAssemblyService.getJavaType(a, td.getArguments().stream().map(ar -> ar.getJavaType()).collect(Collectors.toList()));
					td.setJavaType(jta);

					return td;
				}).orElseGet(() -> null);
	}

	private TypeDescriptor createTypeDescriptor(JavaAssemblyModel javaAssembly, List<JavaTypeModel> typearguments) {
		TypeDescriptor result = new TypeDescriptor();

		result.setJavaAssembly(javaAssembly);
		result.setJavaType(javaAssemblyService.getJavaType(javaAssembly, typearguments));

		return result;
	}

	private JavaAssemblyModel mapJavaAssemblyModel(boolean isAsterisk, NameExpr importname, String name) {
		JavaAssemblyModel result = null;

		if (isAsterisk) {
			result = javaAssemblyService.getJavaAssemblyByAggregate(String.format("%s.%s", importname.toStringWithoutComments(), name));
		} else if (importname.getName().equals(name)){
			result = javaAssemblyService.getJavaAssemblyByAggregate(importname.toStringWithoutComments());
			//TODO create Java Object if it does not exist yet
		}

		return result;
	}

	private TypeDescriptor associateType(SequenceParserContext ctx, TypeDescriptor typedescriptor) {
		if (typedescriptor != null && typedescriptor.getJavaAssembly().getJavaObjectTypeID().equals(javaAssemblyService.getInterfaceType().getPK())) {
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
				//typedescriptor = null;
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

	public JavaTypeService getJavaTypeService() {
		return javaTypeService;
	}

	public void setJavaTypeService(JavaTypeService javaTypeService) {
		this.javaTypeService = javaTypeService;
	}

}
