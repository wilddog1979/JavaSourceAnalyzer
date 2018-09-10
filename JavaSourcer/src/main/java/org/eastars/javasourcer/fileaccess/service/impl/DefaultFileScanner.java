package org.eastars.javasourcer.fileaccess.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eastars.javasourcer.fileaccess.service.FileScanner;
import org.springframework.stereotype.Service;

@Service
public class DefaultFileScanner implements FileScanner {

	private List<File> findFiles(List<File> result, File folder, String pattern) {
		for (File subentry : folder.listFiles(f -> f.isDirectory() || (f.isFile() && f.getName().matches(pattern)))) {
			if (subentry.isDirectory()) {
				findFiles(result, subentry, pattern);
			} else {
				result.add(subentry);
			}
		}
		return result;
	}
	
	@Override
	public List<File> getModuleContent(File folder, String pattern) {
		return findFiles(new ArrayList<>(), folder, pattern);
	}
	
}
