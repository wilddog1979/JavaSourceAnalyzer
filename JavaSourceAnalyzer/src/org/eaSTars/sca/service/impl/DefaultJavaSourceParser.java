package org.eaSTars.sca.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eaSTars.sca.dao.JavaModuleDAO;
import org.eaSTars.sca.gui.ProgressListener;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.sca.service.AssemblyParserContext;
import org.eaSTars.sca.service.JavaParserException;
import org.eaSTars.sca.service.JavaSourceParser;
import org.eaSTars.sca.service.ModuleInfo;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

public class DefaultJavaSourceParser extends AbstractJavaParser implements JavaSourceParser {

	private ProgressListener progressListener;
	
	private JavaModuleDAO javaModuleDAO;
	
	private Map<ModuleInfo, List<File>> modulecontent = new HashMap<ModuleInfo, List<File>>();
	
	@Override
	public void process(List<ModuleInfo> modules) {
		int filecount = 0;
		int processedcount = 0;
		
		for (ModuleInfo moduleinfo : modules) {
			List<File> files = new ArrayList<File>();
			for (String subdirs : moduleinfo.getSubdirs()) {
				files.addAll(fileCollector(new File(moduleinfo.getBasedir(), subdirs)));
			}
			filecount += files.size();
			modulecontent.put(moduleinfo, files);
			javaModuleDAO.createJavaModule(moduleinfo.getName(), true, moduleinfo.getBasedir().getAbsolutePath());
		}
		
		progressListener.setOverallCount(filecount);
		
		long starttime = System.currentTimeMillis();
		long averagetime = 0;

		for (ModuleInfo module : modules) {
			List<File> files = modulecontent.get(module);

			int subtotal = 0;
			progressListener.setSubprogressCount(files.size());
			JavaModuleModel javamodule = javaModuleDAO.getModuleByName(module.getName());
			for (File file : files) {
				progressListener.setStatusText("("+((System.currentTimeMillis() - starttime) / 1000)+"s - "+averagetime+"ms - "+((filecount - processedcount) * averagetime / 1000)+"s) "+module.getName()+" - "+file.getName());

				processFile(javamodule, file);
				averagetime = (System.currentTimeMillis() - starttime) / (++processedcount);

				progressListener.setOverallStep(processedcount);
				progressListener.setSubprogressStep(++subtotal);
			}
		}

		modulecontent.entrySet().forEach(module -> {

		});

		progressListener.setStatusText("");
	}
	
	@Override
	public JavaAssemblyModel processForwardReference(String name) {
		String filename = name.replaceAll("\\.", File.separator.equals("\\") ? "\\\\" : File.separator)+".java";

		return modulecontent.entrySet().stream()
				.map(m -> m.getValue().stream()
						.filter(f -> f.getAbsolutePath().endsWith(filename))
						.findFirst()
						.map(r -> Optional.ofNullable(getJavaAssemblyDAO().getAssemblyByAggregate(name))
								.orElseGet(() -> processFile(javaModuleDAO.getModuleByName(m.getKey().getName()), r)))
						.orElseGet(() -> null))
				.filter(f -> f != null)
				.findFirst().orElseGet(() -> null);
	}
	
	@Override
	public JavaModuleModel getModuleOfReference(String name) {
		String filename = name.replaceAll("\\.", File.separator.equals("\\") ? "\\\\" : File.separator)+".java";
		
		return modulecontent.entrySet().stream()
		.filter(m -> m.getValue().stream()
				.filter(f -> f.getAbsolutePath().endsWith(filename))
				.findFirst().isPresent())
		.findFirst()
		.map(m -> javaModuleDAO.getModuleByName(m.getKey().getName()))
		.orElseGet(() -> null);
	}
	
	private List<File> fileCollector(File dir) {
		List<File> result = new ArrayList<File>();
		for(File subentry : dir.listFiles()) {
			if (subentry.isDirectory()) {
				result.addAll(fileCollector(subentry));
			} else if (subentry.isFile() && subentry.getName().endsWith(".java")) {
				result.add(subentry);
			}
		}
		return result;
	}
	
	private JavaAssemblyModel processFile(JavaModuleModel module, File file) {
		try {
			AssemblyParserContext ctx = new AssemblyParserContext();
			CompilationUnit cu = JavaParser.parse(file);
			
			ctx.setJavaModule(module);
			ctx.setJavaAssembly(Optional.ofNullable(cu.getPackage()).map(pd -> createJavaPackageStructure(pd.getName(), true)).orElse(null));
			ctx.setImports(cu.getImports());
			
			getJavaDeclarationParser().processBodyDeclarations(ctx, file.getAbsolutePath().substring(module.getPath().length()), cu.getTypes());
			return ctx.getJavaAssembly();
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
