package org.eaSTars.sca.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eaSTars.sca.dao.JavaModuleDAO;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaMethodModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.model.JavaObjectTypeModel;
import org.eaSTars.sca.model.JavaTypeModel;
import org.eaSTars.sca.model.JavaTypeParameterModel;
import org.eaSTars.sca.service.AssemblyParserContext;
import org.eaSTars.sca.service.JavaBinaryParser;
import org.eaSTars.sca.service.JavaDeclarationParser;
import org.eaSTars.sca.service.JavaSourceParser;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EmptyMemberDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;

public class DefaultJavaDeclarationParser extends AbstractJavaParser implements JavaDeclarationParser {

	private static final QualifiedNameExpr JAVALANGEXPR = new QualifiedNameExpr(new NameExpr("java"), "lang");

	private JavaModuleDAO javaModuleDAO;
	
	private JavaSourceParser javaSourceParser;
	
	private JavaBinaryParser javaBinaryParser;
	
	private JavaModuleModel javaRuntimeModule;
	
	private JavaAssemblyModel resolveBinaryReference(QualifiedNameExpr nameexpr, JavaModuleModel module) {
		JavaAssemblyModel result = getJavaAssemblyDAO().getAssemblyByAggregate(nameexpr.toStringWithoutComments());
		if (result == null) {
			try {
				Class<?> clazz = this.getClass().getClassLoader().loadClass(nameexpr.toStringWithoutComments());
				result = javaBinaryParser.parse(nameexpr, clazz, module);
			} catch (ClassNotFoundException e1) {
			}
		}
		return result;
	}
	
	private NameExpr createQualifiedNameExpr(ClassOrInterfaceType coit) {
		if (coit.getScope() != null) {
			return new QualifiedNameExpr(createQualifiedNameExpr(coit.getScope()), coit.getName());
		} else {
			return new NameExpr(coit.getName()); 
		}
	}
	
	private JavaAssemblyModel resolveName(AssemblyParserContext ctx, ClassOrInterfaceType coit, List<JavaTypeParameterModel> javamethodtypeparameters, JavaObjectTypeModel objecttype) {
		boolean confirmed = true;
		if (objecttype == null) {
			confirmed = false;
			objecttype = getJavaClassType();
		}
		
		JavaAssemblyModel result = null;
		
		if (coit.getScope() != null) {
			result = resolveBinaryReference((QualifiedNameExpr)createQualifiedNameExpr(coit), getJavaRuntimeModule());
		} else {
			// check if this is a method type parameter
			if (javamethodtypeparameters != null) {
				result = javamethodtypeparameters.stream()
				.filter(jmtp -> jmtp.getName().equals(coit.getName()))
				.findFirst()
				.map(jmtp -> createOtherStructure(null, coit.getName(), true, ctx.getJavaModule(), getJavaClassType(), null))
				.orElseGet(() -> null);
			}
			
			// check if this is an assembly type parameter
			if (result == null) {
				//TODO replace parent with the actual assembly
				JavaTypeParameterModel jtpm = getJavaTypeDAO().getJavaTypeParameterByAssembly(ctx.getJavaAssembly(), coit.getName());
				if (jtpm != null) {
					result = createOtherStructure(null, coit.getName(), true, ctx.getJavaModule(), getJavaClassType(), null);
				}
			}
		
			// check java.lang first
			if (result == null) {
				result = resolveBinaryReference(new QualifiedNameExpr(JAVALANGEXPR, coit.getName()), getJavaRuntimeModule());
			}
			
			// check import entries
			if (result == null) {
				for (ImportDeclaration importentry : ctx.getImports()) {
					if (importentry.isAsterisk()) {
						result = resolveBinaryReference(new QualifiedNameExpr(importentry.getName(),coit.getName()), getJavaRuntimeModule());
						if (result != null) {
							break;
						}
					} else if (importentry.getName().getName().equals(coit.getName())) {
						result = createOtherStructure(importentry.getName(), confirmed, ctx.getJavaModule(), objecttype, null);
						break;
					}
				}
			}
			
			// check if it was already processed and it was in this package
			if (result == null) {
				result = getJavaAssemblyDAO().getAssemblyByAggregate(ctx.getParentJavaAssembly().getAggregate()+"."+coit.getName());
			}
		}
		
		if (result == null) {
			result = javaSourceParser.processForwardReference(ctx.getParentJavaAssembly().getAggregate()+"."+coit.getName(), ctx.getJavaModule());
			if (result == null) {
				System.out.println("early reference to "+ctx.getParentJavaAssembly().getAggregate()+"."+coit.getName()+", not found");
				// it must be here but probably the file is not on class path or in any of the source folders
				result = createOtherStructure(ctx.getParentJavaAssembly(), coit.getName(), confirmed, ctx.getJavaModule(), objecttype, null);
			}
		}
		
		return result;
	}
	
	private JavaTypeModel processType(AssemblyParserContext ctx, List<JavaTypeParameterModel> javamethodtypeparameters, Type type, JavaObjectTypeModel objtype) {
		JavaTypeModel result = null;
		if (type instanceof ReferenceType) {
			ReferenceType rt = (ReferenceType) type;
			result = processType(ctx, javamethodtypeparameters, rt.getType(), null);
		} else if (type instanceof ClassOrInterfaceType) {
			ClassOrInterfaceType coit = (ClassOrInterfaceType) type;
			
			//JavaAssemblyModel javaassembly = resolveName(parent, coit.getName(), javamethodtypeparameters, imports, objtype, module);
			JavaAssemblyModel javaassembly = resolveName(ctx, coit, javamethodtypeparameters, objtype);
			List<JavaTypeModel> typeargs = coit.getTypeArgs().stream()
			.map(typearg -> processType(ctx, javamethodtypeparameters, typearg, null))
			.collect(Collectors.toList());
			
			result = getJavaTypeDAO().createJavaType(javaassembly, typeargs);
		} else if (type instanceof PrimitiveType) {
			result = getJavaTypeDAO().createJavaType(this.createOtherStructure(new QualifiedNameExpr(JAVALANGEXPR, ((PrimitiveType)type).getType().name()), true, ctx.getJavaModule(), getJavaClassType(), null), Collections.emptyList());
		} else if (type instanceof VoidType) {
			result = getJavaTypeDAO().createJavaType(this.createOtherStructure(null, "void", true, ctx.getJavaModule(), getJavaClassType(), null), Collections.emptyList());
		} else if (type instanceof WildcardType) {
			WildcardType wt = (WildcardType) type;
			
			result = getJavaTypeDAO().createJavaType(this.createOtherStructure(null, "*", true, ctx.getJavaModule(), getJavaClassType(), null), Collections.emptyList());
			Optional.ofNullable(wt.getSuper()).ifPresent(sp -> processType(ctx, javamethodtypeparameters, sp, null));
			Optional.ofNullable(wt.getExtends()).ifPresent(ex -> processType(ctx, javamethodtypeparameters, ex, null));
		} else {
			System.out.println(type.getClass().getName());
		}
		
		return result;
	}
	
	private JavaMethodModel processMethod(AssemblyParserContext ctx, int modifiers, Type type, List<TypeParameter> typeparameters, String methodname, List<Parameter> parameters, List<ReferenceType> throwstypes) {
		List<JavaTypeParameterModel> javatypeparameters = typeparameters.stream()
		.map(typeparam -> this.getJavaTypeDAO().createJavaTypeParameter(
				typeparam.getName(),
				typeparam.getTypeBound().stream()
				.map(tp -> this.processType(ctx, null, tp, null))
				.collect(Collectors.toList())))
		.collect(Collectors.toList());
		
		JavaTypeModel javatype = Optional.ofNullable(type).map(t -> processType(ctx, javatypeparameters, t, null)).orElseGet(() -> null);
		
		List<JavaTypeModel> signature = parameters.stream().map(parameter -> processType(ctx, javatypeparameters, parameter.getType(), null)).collect(Collectors.toList());
		JavaMethodModel jmm = getJavaBodyDeclarationDAO().getJavaMethods(ctx.getJavaAssembly(), methodname, signature.size()).stream()
		.filter(method -> getJavaBodyDeclarationDAO().getJavaMethodParameters(method).stream()
				.filter(methodparam -> methodparam.getOrderNumber() >= 0 &&
				methodparam.getOrderNumber() < signature.size() &&
				signature.get(methodparam.getOrderNumber()).getPK().equals(methodparam.getJavaTypeID()))
				.count() == signature.size())
		.findFirst()
		.orElseGet(() -> {
			return getJavaBodyDeclarationDAO().createMethod(ctx.getJavaAssembly(), modifiers, javatype, methodname, parameters.size());
		});
		int count[] = {0};
		parameters.forEach(parameter -> {
			this.getJavaBodyDeclarationDAO().createJavaMethodParameter(jmm, parameter.getName(), count[0],
					signature.get(count[0]++));
		});
		
		javatypeparameters.forEach(jtp -> this.getJavaTypeDAO().createJavaMethodTypeParameter(jmm, jtp));

		throwstypes.forEach(methodthrow -> getJavaBodyDeclarationDAO().createJavathrows(jmm, processType(ctx, javatypeparameters, methodthrow, null)));
		
		return jmm;
	}
	
	private void processBodyDeclarations(AssemblyParserContext ctx, List<BodyDeclaration> bodydeclarations) {
		for (BodyDeclaration bodydeclaration : bodydeclarations) {
			if (bodydeclaration instanceof FieldDeclaration) {
				FieldDeclaration fd = (FieldDeclaration) bodydeclaration;
				
				JavaTypeModel javatype = processType(ctx, null, fd.getType(), null);
				fd.getVariables().stream()
				.forEach(variable -> {
					getJavaBodyDeclarationDAO().createJavaField(ctx.getJavaAssembly(), fd.getModifiers(), javatype, variable.getId().getName());
				});
			} else if (bodydeclaration instanceof MethodDeclaration) {
				MethodDeclaration md = (MethodDeclaration) bodydeclaration;
				
				processMethod(ctx, md.getModifiers(), md.getType(), md.getTypeParameters(), md.getName(), md.getParameters(), md.getThrows());
			} else if (bodydeclaration instanceof ConstructorDeclaration) {
				ConstructorDeclaration cd = (ConstructorDeclaration) bodydeclaration;
				
				processMethod(ctx, cd.getModifiers(), null, cd.getTypeParameters(), cd.getName(), cd.getParameters(), cd.getThrows());
			} else if (bodydeclaration instanceof InitializerDeclaration) {
				
				getJavaBodyDeclarationDAO().createMethod(ctx.getJavaAssembly(), 0, null, "<clinit>", 0);
			} else if (bodydeclaration instanceof ClassOrInterfaceDeclaration) {
				AssemblyParserContext innerctx = new AssemblyParserContext();
				innerctx.setParentJavaAssembly(ctx.getJavaAssembly());
				innerctx.setImports(ctx.getImports());
				parse(innerctx, (ClassOrInterfaceDeclaration) bodydeclaration);
			} else if (bodydeclaration instanceof EnumDeclaration) {
				AssemblyParserContext innerctx = new AssemblyParserContext();
				innerctx.setParentJavaAssembly(ctx.getJavaAssembly());
				innerctx.setImports(ctx.getImports());
				parse(innerctx, (EnumDeclaration)bodydeclaration);
			} else if (bodydeclaration instanceof EmptyMemberDeclaration) {
				// nothing to do
			} else {
				System.out.println(bodydeclaration.getClass().getName());
			}
		}
	}
	
	@Override
	public JavaAssemblyModel parse(AssemblyParserContext ctx, ClassOrInterfaceDeclaration coid) {
		JavaObjectTypeModel objecttype = coid.isInterface() ? getJavaInterfaceType() : getJavaClassType();
		ctx.setJavaAssembly(createOtherStructure(ctx.getParentJavaAssembly(), coid.getName(), true, ctx.getJavaModule(), objecttype, coid.getModifiers()));
		JavaAssemblyModel javaclassorinterface = createOtherStructure(ctx.getParentJavaAssembly(), coid.getName(), true, ctx.getJavaModule(), objecttype, coid.getModifiers());

		coid.getTypeParameters().stream()
		.forEach(typeparam -> {
			List<JavaTypeModel> typeBounds = typeparam.getTypeBound().stream()
			.map(tb -> this.processType(ctx, null, tb, null))
			.collect(Collectors.toList());
			
			JavaTypeParameterModel typeparameter = this.getJavaTypeDAO().createJavaTypeParameter(typeparam.getName(), typeBounds);
			this.getJavaTypeDAO().createJavaAssemblyTypeParameter(javaclassorinterface, typeparameter);
		});
		
		coid.getExtends().stream()
		.forEach(coidextends ->{
			JavaTypeModel javatype = processType(ctx, null, coidextends, objecttype);
			getJavaAssemblyDAO().createExtends(javaclassorinterface, javatype);
		});
		
		coid.getImplements().stream()
		.forEach(coidimplements -> {
			JavaTypeModel javatype = processType(ctx, null, coidimplements, getJavaInterfaceType());
			getJavaAssemblyDAO().createImplements(javaclassorinterface, javatype);
		});
		
		processBodyDeclarations(ctx, coid.getMembers());
		
		return javaclassorinterface;
	}

	@Override
	public JavaAssemblyModel parse(AssemblyParserContext ctx, EnumDeclaration ed) {
		ctx.setJavaAssembly(createOtherStructure(ctx.getParentJavaAssembly(), ed.getName(), true, ctx.getJavaModule(), getJavaEnumType(), ed.getModifiers()));
		JavaAssemblyModel javaenum = createOtherStructure(ctx.getParentJavaAssembly(), ed.getName(), true, ctx.getJavaModule(), getJavaEnumType(), ed.getModifiers());

		ed.getImplements().stream()
		.forEach(edimplements -> {
			JavaTypeModel javatype = processType(ctx, null, edimplements, getJavaInterfaceType());
			getJavaAssemblyDAO().createImplements(javaenum, javatype);
		});
		
		processBodyDeclarations(ctx, ed.getMembers());
		
		return javaenum;
	}

	public JavaModuleModel getJavaRuntimeModule() {
		if (javaRuntimeModule == null) {
			javaRuntimeModule = javaModuleDAO.createJavaModule("Java Runtime");
		}
		return javaRuntimeModule;
	}

	public JavaModuleDAO getJavaModuleDAO() {
		return javaModuleDAO;
	}

	public void setJavaModuleDAO(JavaModuleDAO javaModuleDAO) {
		this.javaModuleDAO = javaModuleDAO;
	}

	public JavaSourceParser getJavaSourceParser() {
		return javaSourceParser;
	}

	public void setJavaSourceParser(JavaSourceParser javaSourceParser) {
		this.javaSourceParser = javaSourceParser;
	}

	public JavaBinaryParser getJavaBinaryParser() {
		return javaBinaryParser;
	}

	public void setJavaBinaryParser(JavaBinaryParser javaBinaryParser) {
		this.javaBinaryParser = javaBinaryParser;
	}

}
