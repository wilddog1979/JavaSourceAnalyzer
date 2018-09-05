package org.eastars.javasourcer.fileaccess.service;

import java.io.File;
import java.util.List;

public interface FileScanner {

	public List<File> findFiles(File folder, String pattern);
	
}
