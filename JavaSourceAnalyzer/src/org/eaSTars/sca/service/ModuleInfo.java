package org.eaSTars.sca.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModuleInfo {

	private String name;
	
	private File basedir;
	
	private List<String> subdirs = new ArrayList<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getBasedir() {
		return basedir;
	}

	public void setBasedir(File basedir) {
		this.basedir = basedir;
	}

	public List<String> getSubdirs() {
		return subdirs;
	}
}
