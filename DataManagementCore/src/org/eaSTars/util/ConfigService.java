package org.eaSTars.util;

public interface ConfigService {

	public String getProperty(String key);
	
	public String getProperty(String key, String defaultValue);
	
	public String getPackageSelector();
	
	public void setPackageSelector(String packageSelector);
}
