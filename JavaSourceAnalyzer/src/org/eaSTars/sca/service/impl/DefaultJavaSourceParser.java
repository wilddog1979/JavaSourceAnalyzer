package org.eaSTars.sca.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eaSTars.sca.dao.JavaModuleDAO;
import org.eaSTars.sca.gui.ProgressListener;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.service.JavaParserException;
import org.eaSTars.sca.service.JavaSourceParser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;

public class DefaultJavaSourceParser extends AbstractJavaParser implements JavaSourceParser {

	private ProgressListener progressListener;
	
	private JavaModuleDAO javaModuleDAO;
	
	private Map<String, List<File>> processMap;
	
	@Override
	public void process(Map<String, List<File>> modules) {
		this.processMap = modules;
		int grandtotal[] = {0, 0};
		Map<String, List<File>> modulecontent = modules.keySet().stream()
		.collect(Collectors.toMap(modulename -> modulename, modulename -> {
			List<File> files = new ArrayList<File>();
			modules.get(modulename).forEach(dir -> fileCollector(dir, files));
			grandtotal[0] += files.size();
			return files;
		}));
		
		progressListener.setOverallCount(grandtotal[0]);
		
		long starttime = System.currentTimeMillis();
		long averagetime[] = {0};
		modulecontent.keySet().forEach(modulename -> {
			int subtotal[] = {0};
			List<File> files = modulecontent.get(modulename); 
			progressListener.setSubprogressCount(files.size());
			JavaModuleModel javamodule = javaModuleDAO.createJavaModule(modulename);
			files.forEach(file -> {
				progressListener.setStatusText("("+averagetime[0]+"ms - "+((grandtotal[0] - grandtotal[1]) * averagetime[0] / 1000)+"s) "+modulename+" - "+file.getName());
				
				processFile(javamodule, file);
				averagetime[0] = (System.currentTimeMillis() - starttime) / (++grandtotal[1]);
				
				progressListener.setOverallStep(grandtotal[1]);
				progressListener.setSubprogressStep(++subtotal[0]);
			});
		});
		
		progressListener.setStatusText("");
	}
	
	@Override
	public JavaAssemblyModel processForwardReference(String name, JavaModuleModel module) {
		String filename = name.replaceAll("\\.", /*File.separator*/"/")+".java";
		File file = processMap.values().stream()
		.map(l -> l.stream().map(f -> new File(f, filename))
				.filter(f -> f.exists() && f.isFile()).findFirst())
		.filter(f -> f.isPresent())
		.findFirst().map(f -> f.get()).orElse(null);
		
		if (file != null) {
			// check if it was already processed - to avoid StackOverflow
			JavaAssemblyModel result = getJavaAssemblyDAO().getAssemblyByAggregate(name);
			if (result == null) {
				processFile(module, file);
				return getJavaAssemblyDAO().getAssemblyByAggregate(name);
			} else {
				return result;
			}
		} else {
			return null;
		}
	}
	
	private List<File> fileCollector(File dir, List<File> files) {
		Arrays.asList(dir.listFiles())
		.forEach(subentry -> {
			if (subentry.isDirectory()) {
				fileCollector(subentry, files);
			} else if (subentry.isFile() && subentry.getName().endsWith(".java")) {
				files.add(subentry);
			}
		});
		return files;
	}
	
	private void processFile(JavaModuleModel module, File file) {
		try {
			CompilationUnit cu = JavaParser.parse(file);
			
			JavaAssemblyModel javapackage = Optional.ofNullable(cu.getPackage()).map(pd -> createJavaPackageStructure(pd.getName(), true)).orElse(null);
			List<ImportDeclaration> imports = cu.getImports();
			
			cu.getTypes().forEach(type -> {
				if (type instanceof ClassOrInterfaceDeclaration) {
					getJavaDeclarationParser().parse(javapackage, imports, (ClassOrInterfaceDeclaration)type, module);
				} else if (type instanceof EnumDeclaration) {
					getJavaDeclarationParser().parse(javapackage, imports, (EnumDeclaration)type, module);
				}
			});
		} catch (ParseException | IOException e) {
			throw new JavaParserException(e);
		}
	}
	
	public ProgressListener getProgressListener() {
		return progressListener;
	}

	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	public JavaModuleDAO getJavaModuleDAO() {
		return javaModuleDAO;
	}

	public void setJavaModuleDAO(JavaModuleDAO javaModuleDAO) {
		this.javaModuleDAO = javaModuleDAO;
	}
	
}
