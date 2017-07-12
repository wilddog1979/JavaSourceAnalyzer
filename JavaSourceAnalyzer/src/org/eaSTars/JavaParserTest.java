package org.eaSTars;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eaSTars.sca.dao.JavaAssemblyDAO;
import org.eaSTars.sca.dao.JavaTypeDAO;
import org.eaSTars.sca.gui.SourceCodeAnalizerGUI;
import org.eaSTars.sca.model.JavaAssemblyModel;
import org.eaSTars.sca.model.JavaTypeModel;
import org.eaSTars.sca.service.JavaDeclarationParser;
import org.eaSTars.sca.service.JavaSourceParser;
import org.eaSTars.sca.service.ModuleInfo;
import org.eaSTars.sca.service.SCADatabaseMaintenanceService;
import org.eaSTars.util.ConfigService;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class JavaParserTest {
	
	private static final String APP_INITDATABASE = "app.conf.initdatabase";
	
	private static final String APP_PACKAGE_KEY = "app.conf.package";
	
	private static final String APP_TESTTYPE_KEY = "app.conf.testtype";
	
	private static final String APP_MODULS_KEY = "app.conf.$package$.modules";
	
	private static final String MODULE_SELECTOR = "\\$module\\$";
	
	private static final String APP_BASEDIR_KEY = "app.conf.$package$.$module$.basedir";

	private static final String APP_SUBDIRS_KEY = "app.conf.$package$.$module$.subdirs";
	
	private static final String APP_EXTERNALLIBRARY_LIST_KEY = "app.conf.ExternalLibrary.list";
	
	private static final String APP_EXTERNALLIBRARY_KEY = "app.conf.ExternalLibrary.$library$";
	
	private static final String LIBRARY_SELECTOR = "\\$library\\$";
	
	private enum TestType {
		Simple,
		Complex;
	}
	
	private ConfigurableApplicationContext context = new FileSystemXmlApplicationContext("resources/JavaSourceAnalyzerContext.xml");
	
	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		new JavaParserTest();
	}
	
	private JavaParserTest() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		try {
			ConfigService configService = context.getBean(ConfigService.class);
			
			if (configService.getProperty(APP_INITDATABASE, "false").equals("true")) {
				SCADatabaseMaintenanceService dblayer = context.getBean(SCADatabaseMaintenanceService.class);
				dblayer.init();
			}
			
			configService.setPackageSelector(configService.getProperty(APP_PACKAGE_KEY, ""));
			
			switch (TestType.valueOf(configService.getProperty(APP_TESTTYPE_KEY, "Simple"))) {
			case Simple:
				runSimpleTest();
				break;
			case Complex:
				runComplexTest();
				break;
			}
			
		} finally {
			context.getBean(SourceCodeAnalizerGUI.class).completed();
			context.close();
		}
	}
	
	private void runSimpleTest() {
		JavaAssemblyDAO javaAssemblyDAO = context.getBean(JavaAssemblyDAO.class);
		
		JavaAssemblyModel java = new JavaAssemblyModel();
		java.setName("java");
		java.setJavaObjectTypeID(javaAssemblyDAO.getPackageObjectType().getPK());
		java.setAggregate("java");
		java.setConfirmed(true);
		javaAssemblyDAO.saveModel(java);
		
		JavaAssemblyModel lang = new JavaAssemblyModel();
		lang.setName("lang");
		lang.setJavaObjectTypeID(javaAssemblyDAO.getPackageObjectType().getPK());
		lang.setAggregate("java.lang");
		lang.setParentAssemblyID(java.getPK());
		lang.setConfirmed(true);
		javaAssemblyDAO.saveModel(lang);
		
		JavaAssemblyModel str = new JavaAssemblyModel();
		str.setName("String");
		str.setJavaObjectTypeID(javaAssemblyDAO.getClassObjectType().getPK());
		str.setAggregate("java.lang.String");
		str.setParentAssemblyID(lang.getPK());
		str.setConfirmed(true);
		javaAssemblyDAO.saveModel(str);
		
		JavaAssemblyModel util = new JavaAssemblyModel();
		util.setName("util");
		util.setJavaObjectTypeID(javaAssemblyDAO.getPackageObjectType().getPK());
		util.setAggregate("java.util");
		util.setParentAssemblyID(java.getPK());
		util.setConfirmed(true);
		javaAssemblyDAO.saveModel(util);
		
		JavaAssemblyModel lst = new JavaAssemblyModel();
		lst.setName("List");
		lst.setJavaObjectTypeID(javaAssemblyDAO.getClassObjectType().getPK());
		lst.setAggregate("java.util.List");
		lst.setParentAssemblyID(util.getPK());
		lst.setConfirmed(true);
		javaAssemblyDAO.saveModel(lst);
		
		JavaTypeDAO javaTypeDAO = context.getBean(JavaTypeDAO.class);
		
		JavaTypeModel strtype = javaTypeDAO.createJavaType(str, Collections.emptyList());
		
		javaTypeDAO.createJavaType(lst, Arrays.asList(strtype));
		
		JavaAssemblyModel map = new JavaAssemblyModel();
		map.setName("Map");
		map.setJavaObjectTypeID(javaAssemblyDAO.getClassObjectType().getPK());
		map.setAggregate("java.util.Map");
		map.setParentAssemblyID(util.getPK());
		map.setConfirmed(true);
		javaAssemblyDAO.saveModel(map);
		
		javaTypeDAO.createJavaType(map, Arrays.asList(strtype, strtype));
		
		javaTypeDAO.createJavaType(map, Arrays.asList(strtype, strtype));
		
		JavaAssemblyModel hmap = new JavaAssemblyModel();
		hmap.setName("HashMap");
		hmap.setJavaObjectTypeID(javaAssemblyDAO.getClassObjectType().getPK());
		hmap.setAggregate("java.util.HashMap");
		hmap.setParentAssemblyID(util.getPK());
		hmap.setConfirmed(true);
		javaAssemblyDAO.saveModel(hmap);
		
		javaTypeDAO.createJavaType(hmap, Arrays.asList(strtype, strtype));
	}
	
	private void runComplexTest() throws ClassNotFoundException, SQLException {
		ConfigService configService = context.getBean(ConfigService.class);
		
		JavaSourceParser parser = context.getBean(JavaSourceParser.class);
		
		JavaDeclarationParser javaDeclarationParser = context.getBean(JavaDeclarationParser.class);
		
		Arrays.asList(configService.getProperty(APP_EXTERNALLIBRARY_LIST_KEY, "").split("\\|")).stream()
		.forEach(lib -> javaDeclarationParser.registerLibrary(lib, Arrays.asList(configService.getProperty(APP_EXTERNALLIBRARY_KEY.replaceAll(LIBRARY_SELECTOR, lib), "").split("\\|"))));
		
		parser.process(Arrays.asList(Optional.ofNullable(configService.getProperty(APP_MODULS_KEY))
				.orElseThrow(() -> new IllegalArgumentException(APP_MODULS_KEY+" configuration is missing"))
				.split("\\|")).stream()
				.map(m -> {
					ModuleInfo moduleinfo = new ModuleInfo();
					moduleinfo.setName(m);
					System.out.printf("Module: %s\n", m);
					String basedirkey = APP_BASEDIR_KEY.replaceAll(MODULE_SELECTOR, m);
					File basedir = new File(Optional.ofNullable(configService.getProperty(basedirkey))
							.orElseThrow(() -> new IllegalArgumentException(basedirkey + " configuration is missing")));
					if (!basedir.exists()) {
						throw new IllegalArgumentException(basedir.getAbsolutePath() + " is not existing");
					} else if (!basedir.isDirectory()) {
						throw new IllegalArgumentException(basedir.getAbsolutePath() + " is not a folder");
					}
					moduleinfo.setBasedir(basedir);
					System.out.printf("\tBasedir: %s\n", basedir.getAbsolutePath());
					String subdirkey = APP_SUBDIRS_KEY.replaceAll(MODULE_SELECTOR, m);
					moduleinfo.getSubdirs().addAll(
							Arrays.asList(Optional.ofNullable(configService.getProperty(subdirkey))
									.orElseThrow(() -> new IllegalArgumentException(subdirkey + " configuration is missing"))
									.split("\\|")).stream()
							.peek(s -> {
								File subdir = new File(moduleinfo.getBasedir(), s);
								if (!subdir.exists()) {
									throw new IllegalArgumentException(subdir.getAbsolutePath()+" is not existing");
								} else if (!subdir.isDirectory()) {
									throw new IllegalArgumentException(subdir.getAbsolutePath()+" is not a folder");
								}
								System.out.printf("\t\t%s\n", s);
							}).collect(Collectors.toList()));
					return moduleinfo;
				}).collect(Collectors.toList()));
	}
}
