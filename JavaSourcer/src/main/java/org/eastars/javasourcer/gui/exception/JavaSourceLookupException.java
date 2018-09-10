package org.eastars.javasourcer.gui.exception;

public class JavaSourceLookupException extends RuntimeException {

	private static final long serialVersionUID = 3896244073089878065L;

	private final String typename;
	
	private final String value;
	
	public JavaSourceLookupException(String typename, String value) {
		this.typename = typename;
		this.value = value;
	}

	/**
	 * @return the typename
	 */
	public String getTypename() {
		return typename;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
}
