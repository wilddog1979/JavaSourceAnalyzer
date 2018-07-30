package org.eaSTars.javasourcer.exception;

import org.springframework.core.convert.ConversionException;

public class JavaSourcerProjectAlreadyExistsException extends ConversionException {

	private static final long serialVersionUID = -3688047358400348772L;
	
	public JavaSourcerProjectAlreadyExistsException(String message) {
		super(String.format("\"%s\" project already exists", message));
	}

}
