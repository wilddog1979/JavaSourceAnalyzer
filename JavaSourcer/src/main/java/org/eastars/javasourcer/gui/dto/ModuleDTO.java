package org.eastars.javasourcer.gui.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModuleDTO implements Serializable {

	private static final long serialVersionUID = -859947360185026897L;

	private String originalName;
	
	private String name;
	
	private List<String> sourceFolders = new ArrayList<>();

	public String getName() {
		return name;
	}

	public ModuleDTO setName(String name) {
		this.name = name;
		return this;
	}

	public List<String> getSourceFolders() {
		return sourceFolders;
	}

	public void setSourceFolders(List<String> sourceFolders) {
		this.sourceFolders = sourceFolders;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}
	
}
