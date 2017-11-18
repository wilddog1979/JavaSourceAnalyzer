package org.eaSTars.util;

public interface ConfigService {

	public String getProperty(String key);
	
	public void removeProperty(String key);
	
	public void setProperty(String key, String value);
	
	public String getProperty(String key, String defaultValue);
	
	public String getPackageSelector();
	
	public void setPackageSelector(String packageSelector);
	
	public void saveConfig();
}
