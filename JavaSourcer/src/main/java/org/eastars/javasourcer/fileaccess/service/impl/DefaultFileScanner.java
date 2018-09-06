package org.eastars.javasourcer.fileaccess.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eastars.javasourcer.fileaccess.service.FileScanner;
import org.springframework.stereotype.Service;

@Service
public class DefaultFileScanner implements FileScanner {

	private void findFiles(List<File> result, File folder, String pattern) {
		for (File subentry : folder.listFiles(f -> f.isDirectory() || (f.isFile() && f.getName().matches(pattern)))) {
			if (subentry.isDirectory()) {
				findFiles(result, subentry, pattern);
			} else {
				result.add(subentry);
			}
		}
	}
	
	@Override
	public List<String> getModuleContent(File folder, String pattern) {
		List<File> content = new ArrayList<>();
		findFiles(content, folder, pattern);
		
		return content.stream().map(File::getAbsolutePath).collect(Collectors.toList());
	}
	
}
