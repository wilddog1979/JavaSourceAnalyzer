package org.eaSTars.dblayer.dao;

public class FilterEntry {

	private String propertyName;
	
	private Object value;

	public FilterEntry(String propertyName, Object value) {
		this.propertyName = propertyName;
		this.value = value;
	}
	
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
