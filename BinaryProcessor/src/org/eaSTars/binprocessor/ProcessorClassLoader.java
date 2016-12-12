package org.eaSTars.binprocessor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ProcessorClassLoader extends ClassLoader {

	private List<File> classpath = new ArrayList<File>();
	
	private Map<String, Class<?>> classcache = new HashMap<String, Class<?>>();
	
	public ProcessorClassLoader(ClassLoader parent) {
		super(parent);
	}
	
	public ProcessorClassLoader(ClassLoader parent, List<File> classpath) {
		this(parent);
		this.classpath = classpath;
	}
	
	private Class<?> scanClassPathFile(File cpentry, String name) {
		Class<?> result = null;
		try {
			ZipFile zfile = new ZipFile(cpentry, ZipFile.OPEN_READ);
			
			ZipEntry zipentry = zfile.getEntry(name.replaceAll("\\.", "/")+".class");
			if (zipentry != null && !zipentry.isDirectory()) {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				InputStream is = zfile.getInputStream(zipentry);
				int data = -1;
				while((data = is.read()) != -1) {
					buffer.write(data);
				}
				is.close();
				buffer.close();

				result = defineClass(name, buffer.toByteArray(), 0, buffer.size());
				classcache.put(name, result);
			}
			
			zfile.close();
		} catch (IOException e) {
		}
		return result;
	}
	
	private Class<?> scanClassPath(String name) {
		Class<?> result = null;
		
		for (File cpentry : classpath) {
			if (cpentry.isDirectory()) {
				
			} else if (cpentry.isFile()) {
				result = scanClassPathFile(cpentry, name);
			}
			
			if (result != null) {
				break;
			}
		}
		
		return result;
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (name.startsWith("java.")) {
			return getParent().loadClass(name);
		} else {
			Class<?> clazz = classcache.get(name);
			if (clazz == null) {
				clazz = scanClassPath(name);
			}
			if (clazz == null) {
				clazz = getParent().loadClass(name);
			}
			return clazz;
		}
	}
	
	public void addClassPathEntry(File entry) {
		classpath.add(entry);
	}

	public List<File> getClasspath() {
		return classpath;
	}
	
	public void emptyCache() {
		classcache.clear();
	}
}
