package org.eaSTars.dblayer.dao;

public class DatabaseConnectionException extends RuntimeException {

	private static final long serialVersionUID = -5464084257467758470L;

	public DatabaseConnectionException() {
		super();
	}

	public DatabaseConnectionException(String message) {
		super(message);
	}

	public DatabaseConnectionException(Throwable cause) {
		super(cause);
	}

	public DatabaseConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatabaseConnectionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
