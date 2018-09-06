package org.eastars.javasourcer.analyzis.dto;

import java.util.ArrayList;
import java.util.List;

public class ModuleContentDTO {

	private String name;
	
	private List<String> files;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the files
	 */
	public List<String> getFiles() {
		if (files == null) {
			files = new ArrayList<>();
		}
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<String> files) {
		this.files = files;
	}
	
}
