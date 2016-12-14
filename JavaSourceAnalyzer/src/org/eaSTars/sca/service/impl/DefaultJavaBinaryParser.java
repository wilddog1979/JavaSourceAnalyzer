package org.eaSTars.sca.service.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.model.JavaTypeModel;
import org.eaSTars.sca.service.JavaBinaryParser;
import org.eaSTars.sca.service.ParserContext;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;

public class DefaultJavaBinaryParser extends AbstractJavaParser implements JavaBinaryParser {

	private static final Logger LOGGER = LogManager.getLogger(DefaultJavaBinaryParser.class);
	
	private NameExpr createQNameExpr(String name) {
		int index = name.lastIndexOf('.');
		if (index == -1) {
			return new NameExpr(name);
		} else {
			return new QualifiedNameExpr(createQNameExpr(name.substring(0, index)), name.substring(index + 1));
		}
	}
	
	private JavaTypeModel createJavaType(Type type, JavaModuleModel module) {
		JavaTypeModel result = null;
		if (type != null) {
			if (type instanceof Class<?>) {
				result = getJavaTypeDAO().createJavaType(parse((Class<?>)type, module), Collections.emptyList());
			} else if (type instanceof ParameterizedType) {
				ParameterizedType t = (ParameterizedType) type;
				
				
				List<JavaTypeModel> typearguments = Arrays.asList(t.getActualTypeArguments()).stream()
				.map(typearg -> createJavaType(typearg, module))
				.collect(Collectors.toList());
				
				JavaAssemblyModel jam = parse(t.getRawType().getClass(), module);
				result = getJavaTypeDAO().createJavaType(jam, typearguments);
			} else if (type instanceof TypeVariable) {
				TypeVariable<?> tv = (TypeVariable<?>) type;
				
				return getJavaTypeDAO().createJavaType(createOtherStructure(null, tv.getName(), true, module, getJavaClassType(), null), Collections.emptyList());
			} else {
				LOGGER.warn("Unimplemented java type: "+type.getClass().getName());
			}
		}
		return result;
	}
	
	private void parseCommon(ParserContext ctx, Class<?> clazz) {
		Optional.ofNullable(createJavaType(clazz.getGenericSuperclass(), ctx.getJavaModule()))
		.ifPresent(t -> getJavaAssemblyDAO().createExtends(ctx.getJavaAssembly(), t));
		
		Arrays.asList(clazz.getGenericInterfaces())
		.forEach(i -> getJavaAssemblyDAO().createImplements(ctx.getJavaAssembly(), createJavaType(i, ctx.getJavaModule())));
		
		Arrays.asList(clazz.getTypeParameters()).stream()
		.map(tv -> createJavaType(tv, ctx.getJavaModule()))
		.collect(Collectors.toList());
	}
	
	private JavaAssemblyModel parseClass(ParserContext ctx, Class<?> clazz) {
		JavaAssemblyModel result = getJavaAssemblyDAO().getAssemblyByAggregate(clazz.getName());
		if (result == null) {
			result = createOtherStructure(createQNameExpr(clazz.getName()), true, ctx.getJavaModule(), getJavaClassType(), clazz.getModifiers());
			ctx.setJavaAssembly(result);
			
			parseCommon(ctx, clazz);
		}
		
		return result;
	}
	
	private JavaAssemblyModel parseInterface(ParserContext ctx, Class<?> clazz) {
		JavaAssemblyModel result = createOtherStructure(createQNameExpr(clazz.getName()), true, ctx.getJavaModule(), getJavaInterfaceType(), clazz.getModifiers());
		
		if (result == null) {
			result = createOtherStructure(createQNameExpr(clazz.getName()), true, ctx.getJavaModule(), getJavaClassType(), clazz.getModifiers());
			ctx.setJavaAssembly(result);
			
			parseCommon(ctx, clazz);
		}
		
		return result;
	}
	
	private JavaAssemblyModel parseEnum(ParserContext ctx, Class<?> clazz) {
		JavaAssemblyModel result = createOtherStructure(createQNameExpr(clazz.getName()), true, ctx.getJavaModule(), getJavaEnumType(), clazz.getModifiers());
		
		if (result == null) {
			result = createOtherStructure(createQNameExpr(clazz.getName()), true, ctx.getJavaModule(), getJavaClassType(), clazz.getModifiers());
			ctx.setJavaAssembly(result);
			
			parseCommon(ctx, clazz);
		}
		
		return result;
	}
	
	private JavaAssemblyModel parseAnnotation(ParserContext ctx, Class<?> clazz) {
		JavaAssemblyModel result = createOtherStructure(createQNameExpr(clazz.getName()), true, ctx.getJavaModule(), getJavaAnnotationType(), clazz.getModifiers());
		
		if (result == null) {
			result = createOtherStructure(createQNameExpr(clazz.getName()), true, ctx.getJavaModule(), getJavaClassType(), clazz.getModifiers());
			ctx.setJavaAssembly(result);
			
			parseCommon(ctx, clazz);
		}
		
		return result;
	}
	
	@Override
	public JavaAssemblyModel parse(Class<?> clazz, JavaModuleModel module) {
		ParserContext ctx = new BinaryParserContext();
		ctx.setJavaModule(module);
		if (clazz.isInterface()) {
			return parseInterface(ctx, clazz);
		} else if (clazz.isEnum()) {
			return parseEnum(ctx, clazz);
		} else if (clazz.isAnnotation()) {
			return parseAnnotation(ctx, clazz);
		} else {
			return parseClass(ctx, clazz);
		}
	}
}
