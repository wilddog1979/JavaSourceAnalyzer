package org.eastars.javasourcer.fileaccess.service;

import java.io.File;
import java.util.List;

public interface FileScanner {

	public List<File> getModuleContent(File folder, String pattern);
	
}
