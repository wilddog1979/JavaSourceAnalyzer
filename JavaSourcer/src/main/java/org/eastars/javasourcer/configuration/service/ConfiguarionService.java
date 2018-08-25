package org.eastars.javasourcer.configuration.service;

public interface ConfiguarionService {

	public String getStringValue(String key);
	
	public Integer getIntegerValue(String key);
	
	public void removeProperty(String key);
	
	public void setProperty(String key, String value);
	
	public String getValue(String key, String defaultValue);
	
	public Integer getValue(String key, Integer defaultValue);
	
	public String getPackageSelector();
	
	public void setPackageSelector(String packageSelector);
	
	public void saveConfig();
	
}
