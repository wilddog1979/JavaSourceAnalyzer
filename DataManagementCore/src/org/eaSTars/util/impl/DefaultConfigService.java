package org.eaSTars.util.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eaSTars.util.ConfigService;

public class DefaultConfigService implements ConfigService {

	private static final String PACKAGE_SELECTOR = "\\$package\\$";
	
	private String propertiesfile = "conf.properties";
	
	private String packageSelector = "";
	
	private Properties properies;
	
	private Properties getProperties() {
		if (properies == null) {
			try {
				properies = new Properties();
				properies.load(new FileInputStream(propertiesfile));
			} catch (IOException e) {
			}
		}
		return properies;
	}
	
	private String getKey(String key) {
		return key.replaceAll(PACKAGE_SELECTOR, packageSelector);
	}
	
	@Override
	public String getProperty(String key) {
		key = getKey(key);
		return getProperties().getProperty(key);
	}
	
	@Override
	public void removeProperty(String key) {
		key = getKey(key);
		getProperties().remove(key);
	}
	
	@Override
	public void setProperty(String key, String value) {
		key = getKey(key);
		getProperties().setProperty(key, value);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		key = getKey(key);
		return getProperties().getProperty(key, defaultValue);
	}
	
	@Override
	public String getPackageSelector() {
		return packageSelector;
	}

	@Override
	public void setPackageSelector(String packageSelector) {
		this.packageSelector = packageSelector;
	}
	
	@Override
	public void saveConfig() {
		if (propertiesfile != null) {
			try {
				properies.store(new FileOutputStream(propertiesfile), null);
			} catch (IOException e) {
			}
		}
	}

	public String getPropertiesfile() {
		return propertiesfile;
	}

	public void setPropertiesfile(String propertiesfile) {
		this.propertiesfile = propertiesfile;
	}
}
