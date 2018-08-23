package org.eaSTars.javasourcer.gui.dto;

import java.util.ArrayList;
import java.util.List;

public class LibraryDTO {

	private String originalName;
	
	private String name;
	
	private List<String> packages;

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPackages() {
		if (packages == null) {
			packages = new ArrayList<>();
		}
		return packages;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}
	
}
