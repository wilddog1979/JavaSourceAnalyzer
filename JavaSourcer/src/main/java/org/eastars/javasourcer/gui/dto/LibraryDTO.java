package org.eastars.javasourcer.gui.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LibraryDTO implements Serializable {

	private static final long serialVersionUID = -7723497702427551223L;

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

	public LibraryDTO setName(String name) {
		this.name = name;
		return this;
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
