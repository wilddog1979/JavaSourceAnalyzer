package org.eaSTars.sca.service;

public class JavaParserException extends RuntimeException {

	private static final long serialVersionUID = -8787183084674593419L;

	public JavaParserException() {
	}

	public JavaParserException(String message) {
		super(message);
	}

	public JavaParserException(Throwable cause) {
		super(cause);
	}

	public JavaParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public JavaParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
