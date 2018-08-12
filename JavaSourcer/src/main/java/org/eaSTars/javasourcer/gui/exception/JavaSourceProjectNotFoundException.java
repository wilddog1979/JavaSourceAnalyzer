package org.eaSTars.javasourcer.gui.exception;

public class JavaSourceProjectNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4975591067607907608L;

	public JavaSourceProjectNotFoundException(String projectname) {
		super(projectname);
	}
}
