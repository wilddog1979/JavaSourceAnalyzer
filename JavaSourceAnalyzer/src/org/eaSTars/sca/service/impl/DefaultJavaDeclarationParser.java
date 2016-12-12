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
	
	private JavaAssemblyModel resolveName(JavaAssemblyModel parent, JavaAssemblyModel javaassembly, ClassOrInterfaceType coit, List<JavaTypeParameterModel> javamethodtypeparameters, List<ImportDeclaration> imports,
			JavaObjectTypeModel objecttype, JavaModuleModel module) {
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
				.map(jmtp -> createOtherStructure(null, coit.getName(), true, module, getJavaClassType(), null))
				.orElseGet(() -> null);
			}
			
			// check if this is an assembly type parameter
			if (result == null) {
				//TODO replace parent with the actual assembly
				JavaTypeParameterModel jtpm = getJavaTypeDAO().getJavaTypeParameterByAssembly(javaassembly, coit.getName());
				if (jtpm != null) {
					result = createOtherStructure(null, coit.getName(), true, module, getJavaClassType(), null);
				}
			}
		
			// check java.lang first
			if (result == null) {
				result = resolveBinaryReference(new QualifiedNameExpr(JAVALANGEXPR, coit.getName()), getJavaRuntimeModule());
			}
			
			// check import entries
			if (result == null) {
				for (ImportDeclaration importentry : imports) {
					if (importentry.isAsterisk()) {
						result = resolveBinaryReference(new QualifiedNameExpr(importentry.getName(),coit.getName()), getJavaRuntimeModule());
						if (result != null) {
							break;
						}
					} else if (importentry.getName().getName().equals(coit.getName())) {
						result = createOtherStructure(importentry.getName(), confirmed, module, objecttype, null);
						break;
					}
				}
			}
			
			// check if it was already processed and it was in this package
			if (result == null) {
				result = getJavaAssemblyDAO().getAssemblyByAggregate(parent.getAggregate()+"."+coit.getName());
			}
		}
		
		if (result == null) {
			result = javaSourceParser.processForwardReference(parent.getAggregate()+"."+coit.getName(), module);
			if (result == null) {
				System.out.println("early reference to "+parent.getAggregate()+"."+coit.getName()+", not found");
				// it must be here but probably the file is not on class path or in any of the source folders
				result = createOtherStructure(parent, coit.getName(), confirmed, module, objecttype, null);
			}
		}
		
		return result;
	}
	
	private JavaTypeModel processType(JavaAssemblyModel parent, JavaAssemblyModel jassembly, List<JavaTypeParameterModel> javamethodtypeparameters, List<ImportDeclaration> imports, Type type, JavaObjectTypeModel objtype, JavaModuleModel module) {
		JavaTypeModel result = null;
		if (type instanceof ReferenceType) {
			ReferenceType rt = (ReferenceType) type;
			result = processType(parent, jassembly, javamethodtypeparameters, imports, rt.getType(), null, module);
		} else if (type instanceof ClassOrInterfaceType) {
			ClassOrInterfaceType coit = (ClassOrInterfaceType) type;
			
			//JavaAssemblyModel javaassembly = resolveName(parent, coit.getName(), javamethodtypeparameters, imports, objtype, module);
			JavaAssemblyModel javaassembly = resolveName(parent, jassembly, coit, javamethodtypeparameters, imports, objtype, module);
			List<JavaTypeModel> typeargs = coit.getTypeArgs().stream()
			.map(typearg -> processType(parent, jassembly, javamethodtypeparameters, imports, typearg, null, module))
			.collect(Collectors.toList());
			
			result = getJavaTypeDAO().createJavaType(javaassembly, typeargs);
		} else if (type instanceof PrimitiveType) {
			result = getJavaTypeDAO().createJavaType(this.createOtherStructure(new QualifiedNameExpr(JAVALANGEXPR, ((PrimitiveType)type).getType().name()), true, module, getJavaClassType(), null), Collections.emptyList());
		} else if (type instanceof VoidType) {
			result = getJavaTypeDAO().createJavaType(this.createOtherStructure(null, "void", true, module, getJavaClassType(), null), Collections.emptyList());
		} else if (type instanceof WildcardType) {
			WildcardType wt = (WildcardType) type;
			
			result = getJavaTypeDAO().createJavaType(this.createOtherStructure(null, "*", true, module, getJavaClassType(), null), Collections.emptyList());
			Optional.ofNullable(wt.getSuper()).ifPresent(sp -> processType(parent, jassembly, javamethodtypeparameters, imports, sp, null, module));
			Optional.ofNullable(wt.getExtends()).ifPresent(ex -> processType(parent, jassembly, javamethodtypeparameters, imports, ex, null, module));
		} else {
			System.out.println(type.getClass().getName());
		}
		
		return result;
	}
	
	private JavaMethodModel processMethod(JavaAssemblyModel parent, List<ImportDeclaration> imports, JavaAssemblyModel javaassembly, int modifiers, Type type, List<TypeParameter> typeparameters, String methodname, List<Parameter> parameters, List<ReferenceType> throwstypes, JavaModuleModel module) {
		List<JavaTypeParameterModel> javatypeparameters = typeparameters.stream()
		.map(typeparam -> this.getJavaTypeDAO().createJavaTypeParameter(
				typeparam.getName(),
				typeparam.getTypeBound().stream()
				.map(tp -> this.processType(parent, javaassembly, null, imports, tp, null, module))
				.collect(Collectors.toList())))
		.collect(Collectors.toList());
		
		JavaTypeModel javatype = Optional.ofNullable(type).map(t -> processType(parent, javaassembly, javatypeparameters, imports, t, null, module)).orElseGet(() -> null);
		
		List<JavaTypeModel> signature = parameters.stream().map(parameter -> processType(parent, javaassembly, javatypeparameters, imports, parameter.getType(), null, module)).collect(Collectors.toList());
		JavaMethodModel jmm = getJavaBodyDeclarationDAO().getJavaMethods(javaassembly, methodname, signature.size()).stream()
		.filter(method -> getJavaBodyDeclarationDAO().getJavaMethodParameters(method).stream()
				.filter(methodparam -> methodparam.getOrderNumber() >= 0 &&
				methodparam.getOrderNumber() < signature.size() &&
				signature.get(methodparam.getOrderNumber()).getPK().equals(methodparam.getJavaTypeID()))
				.count() == signature.size())
		.findFirst()
		.orElseGet(() -> {
			return getJavaBodyDeclarationDAO().createMethod(javaassembly, modifiers, javatype, methodname, parameters.size());
		});
		int count[] = {0};
		parameters.forEach(parameter -> {
			this.getJavaBodyDeclarationDAO().createJavaMethodParameter(jmm, parameter.getName(), count[0],
					signature.get(count[0]++));
		});
		
		javatypeparameters.forEach(jtp -> this.getJavaTypeDAO().createJavaMethodTypeParameter(jmm, jtp));

		throwstypes.forEach(methodthrow -> getJavaBodyDeclarationDAO().createJavathrows(jmm, processType(parent, javaassembly, javatypeparameters, imports, methodthrow, null, module)));
		
		return jmm;
	}
	
	private void processBodyDeclarations(JavaAssemblyModel parent, List<ImportDeclaration> imports, JavaAssemblyModel javaassembly, List<BodyDeclaration> bodydeclarations, JavaModuleModel module) {
		for (BodyDeclaration bodydeclaration : bodydeclarations) {
			if (bodydeclaration instanceof FieldDeclaration) {
				FieldDeclaration fd = (FieldDeclaration) bodydeclaration;
				
				JavaTypeModel javatype = processType(parent, javaassembly, null, imports, fd.getType(), null, module);
				fd.getVariables().stream()
				.forEach(variable -> {
					getJavaBodyDeclarationDAO().createJavaField(javaassembly, fd.getModifiers(), javatype, variable.getId().getName());
				});
			} else if (bodydeclaration instanceof MethodDeclaration) {
				MethodDeclaration md = (MethodDeclaration) bodydeclaration;
				
				processMethod(parent, imports, javaassembly, md.getModifiers(), md.getType(), md.getTypeParameters(), md.getName(), md.getParameters(), md.getThrows(), module);
			} else if (bodydeclaration instanceof ConstructorDeclaration) {
				ConstructorDeclaration cd = (ConstructorDeclaration) bodydeclaration;
				
				processMethod(parent, imports, javaassembly, cd.getModifiers(), null, cd.getTypeParameters(), cd.getName(), cd.getParameters(), cd.getThrows(), module);
			} else if (bodydeclaration instanceof InitializerDeclaration) {
				
				getJavaBodyDeclarationDAO().createMethod(javaassembly, 0, null, "<clinit>", 0);
			} else if (bodydeclaration instanceof ClassOrInterfaceDeclaration) {
				parse(javaassembly, imports, (ClassOrInterfaceDeclaration) bodydeclaration, module);
			} else if (bodydeclaration instanceof EnumDeclaration) {
				parse(javaassembly, imports, (EnumDeclaration)bodydeclaration, module);
			} else if (bodydeclaration instanceof EmptyMemberDeclaration) {
				// nothing to do
			} else {
				System.out.println(bodydeclaration.getClass().getName());
			}
		}
	}
	
	@Override
	public JavaAssemblyModel parse(JavaAssemblyModel parent, List<ImportDeclaration> imports, ClassOrInterfaceDeclaration coid, JavaModuleModel module) {
		JavaObjectTypeModel objecttype = coid.isInterface() ? getJavaInterfaceType() : getJavaClassType();
		JavaAssemblyModel javaclassorinterface = createOtherStructure(parent, coid.getName(), true, module, objecttype, coid.getModifiers());

		coid.getTypeParameters().stream()
		.forEach(typeparam -> {
			List<JavaTypeModel> typeBounds = typeparam.getTypeBound().stream()
			.map(tb -> this.processType(parent, javaclassorinterface, null, imports, tb, null, module))
			.collect(Collectors.toList());
			
			JavaTypeParameterModel typeparameter = this.getJavaTypeDAO().createJavaTypeParameter(typeparam.getName(), typeBounds);
			this.getJavaTypeDAO().createJavaAssemblyTypeParameter(javaclassorinterface, typeparameter);
		});
		
		coid.getExtends().stream()
		.forEach(coidextends ->{
			JavaTypeModel javatype = processType(parent, javaclassorinterface, null, imports, coidextends, objecttype, module);
			getJavaAssemblyDAO().createExtends(javaclassorinterface, javatype);
		});
		
		coid.getImplements().stream()
		.forEach(coidimplements -> {
			JavaTypeModel javatype = processType(parent, javaclassorinterface, null, imports, coidimplements, getJavaInterfaceType(), module);
			getJavaAssemblyDAO().createImplements(javaclassorinterface, javatype);
		});
		
		processBodyDeclarations(parent, imports, javaclassorinterface, coid.getMembers(), module);
		
		return javaclassorinterface;
	}

	@Override
	public JavaAssemblyModel parse(JavaAssemblyModel parent, List<ImportDeclaration> imports, EnumDeclaration ed, JavaModuleModel module) {
		JavaAssemblyModel javaenum = createOtherStructure(parent, ed.getName(), true, module, getJavaEnumType(), ed.getModifiers());

		ed.getImplements().stream()
		.forEach(edimplements -> {
			JavaTypeModel javatype = processType(parent, javaenum, null, imports, edimplements, getJavaInterfaceType(), module);
			getJavaAssemblyDAO().createImplements(javaenum, javatype);
		});
		
		processBodyDeclarations(parent, imports, javaenum, ed.getMembers(), module);
		
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
