package org.eaSTars.binprocessor.service;

public class BinaryParserException extends RuntimeException {

	private static final long serialVersionUID = -2182449922008627196L;

	public BinaryParserException() {
	}

	public BinaryParserException(String message) {
		super(message);
	}

	public BinaryParserException(Throwable cause) {
		super(cause);
	}

	public BinaryParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public BinaryParserException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
