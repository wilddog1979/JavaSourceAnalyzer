package org.eaSTars.util.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.eaSTars.util.ConfigService;

public class DefaultConfigService implements ConfigService {

	private static final String PACKAGE_SELECTOR = "\\$package\\$";
	
	private String packageSelector = "";
	
	private Properties properies;
	
	private Properties getProperties() {
		if (properies == null) {
			try {
				properies = new Properties();
				properies.load(new FileInputStream("conf.properties"));
			} catch (IOException e) {
			}
		}
		return properies;
	}
	
	@Override
	public String getProperty(String key) {
		key = key.replaceAll(PACKAGE_SELECTOR, packageSelector);
		return getProperties().getProperty(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		key = key.replaceAll(PACKAGE_SELECTOR, packageSelector);
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
}
