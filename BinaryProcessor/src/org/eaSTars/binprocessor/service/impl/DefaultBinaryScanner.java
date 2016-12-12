package org.eaSTars.binprocessor.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.ZipFile;

import org.eaSTars.binprocessor.ProcessorClassLoader;
import org.eaSTars.binprocessor.service.BinaryDeclarationParser;
import org.eaSTars.binprocessor.service.BinaryParserException;
import org.eaSTars.binprocessor.service.BinaryScanner;
import org.eaSTars.sca.dao.JavaModuleDAO;
import org.eaSTars.sca.gui.ProgressListener;
import org.eaSTars.sca.model.JavaModuleModel;
import org.eaSTars.util.ConfigService;
import org.springframework.beans.factory.InitializingBean;

public class DefaultBinaryScanner implements BinaryScanner, InitializingBean {

	private static final String BASEDIR_KEY = "app.conf.basedir";
	
	private static final String FILES_KEY = "app.conf.entries";

	private ProgressListener progressListener;
	
	private ConfigService configService;

	private JavaModuleDAO javaModuleDAO;
	
	private BinaryDeclarationParser declarationParser;
	
	private ProcessorClassLoader classLoader = new ProcessorClassLoader(this.getClass().getClassLoader());
	
	@Override
	public void afterPropertiesSet() throws Exception {
		File basedir = new File(configService.getProperty(BASEDIR_KEY));
		Arrays.asList(configService.getProperty(FILES_KEY).split("\\|")).stream()
		.map(s -> new File(basedir, s))
		.filter(f -> f.exists())
		.forEach(f -> {
			if (f.isDirectory() || (f.isFile() && (f.getName().endsWith(".jar") || f.getName().equals(".zip")))) {
				classLoader.addClassPathEntry(f);
			}
		});
	}
	
	@Override
	public void beginScanning() {
		JavaModuleModel module = javaModuleDAO.createJavaModule("Java Runtime");
		
		classLoader.getClasspath()
		.forEach(cpentry -> {
			if (cpentry.isDirectory()) {
				
			} else if (cpentry.isFile()) {
				scanFile(cpentry, module);
			}
		});
	}
	
	private void scanFile(File file, JavaModuleModel module) {
		ZipFile zfile = null;
		try {
			zfile = new ZipFile(file, ZipFile.OPEN_READ);
			
			int grandtotal[] = {0, 0};
			
			Collections.list(zfile.entries()).stream()
					.filter(zentry -> !zentry.isDirectory() && zentry.getName().indexOf('$') == -1 && zentry.getName().endsWith(".class"))
					.forEach(ze -> grandtotal[0]++);
			progressListener.setOverallCount(grandtotal[0]);
			
			long starttime[] = {System.currentTimeMillis()};
			long averagetime[] = {0};
			Collections.list(zfile.entries()).stream()
			.filter(zentry -> !zentry.isDirectory() && zentry.getName().indexOf('$') == -1 && zentry.getName().endsWith(".class"))
			.forEach(zentry -> {
				String classname = zentry.getName().substring(0, zentry.getName().length() - 6).replaceAll("\\/", ".");
				System.out.println(!zentry.isDirectory()+" && "+(zentry.getName().indexOf('$') == -1)+" && "+zentry.getName().endsWith(".class")+" "+zentry.getName());
				progressListener.setStatusText("("+averagetime[0]+"ms - "+((grandtotal[0] - grandtotal[1]) * averagetime[0] / 1000)+"s) "+classname);

				try {
					Class<?> cl = classLoader.loadClass(classname);
					declarationParser.parse(cl, module);
				} catch (ClassNotFoundException e) {
					throw new BinaryParserException(e);
				}

				averagetime[0] = (System.currentTimeMillis() - starttime[0]) / (++grandtotal[1]);
				progressListener.setOverallStep(grandtotal[1]);

				classLoader = new ProcessorClassLoader(this.getClass().getClassLoader(), classLoader.getClasspath());
			});
		} catch (IOException e) {
			throw new BinaryParserException(e);
		} finally {
			if (zfile != null) {
				try {
					zfile.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public ProgressListener getProgressListener() {
		return progressListener;
	}

	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	public ConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	public JavaModuleDAO getJavaModuleDAO() {
		return javaModuleDAO;
	}

	public void setJavaModuleDAO(JavaModuleDAO javaModuleDAO) {
		this.javaModuleDAO = javaModuleDAO;
	}

	public BinaryDeclarationParser getDeclarationParser() {
		return declarationParser;
	}

	public void setDeclarationParser(BinaryDeclarationParser declarationParser) {
		this.declarationParser = declarationParser;
	}
}
