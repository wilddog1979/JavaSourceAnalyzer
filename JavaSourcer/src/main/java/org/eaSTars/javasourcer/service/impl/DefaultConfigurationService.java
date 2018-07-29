package org.eaSTars.javasourcer.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eaSTars.javasourcer.service.ConfiguarionService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

@Service
public class DefaultConfigurationService implements ConfiguarionService, InitializingBean {

	private static final Logger LOGGER = LogManager.getLogger(DefaultConfigurationService.class);
	
	private static final String PACKAGE_SELECTOR = "\\$package\\$";
	
	private String propertiesfile = "conf.properties";
	
	private String packageSelector = "";
	
	private Properties properies = new Properties();

	@Override
	public void afterPropertiesSet() throws Exception {
		try (FileInputStream propertiesfile = new FileInputStream(getPropertiesfile())) {
			properies.load(propertiesfile);
		} catch (FileNotFoundException e) {
			LOGGER.warn("Configuration file does not exist, default values will be used");
		}
	}

	@Override
	public void saveConfig() {
		try (FileOutputStream propertiesfile = new FileOutputStream(getPropertiesfile())) {
			properies.store(propertiesfile, null);
		} catch (IOException e) {
			LOGGER.error("Error while saving configuration", e);
		}
	}
	
	private String substitutePackageSelector(String key) {
		return key.replaceAll(PACKAGE_SELECTOR, packageSelector);
	}
	
	@Override
	public String getStringValue(String key) {
		return getValue(key, (String)null);
	}
	
	@Override
	public Integer getIntegerValue(String key) {
		return getValue(key, (Integer)null);
	}
	
	@Override
	public String getValue(String key, String defaultValue) {
		return properies.getProperty(substitutePackageSelector(key), defaultValue);
	}
	
	@Override
	public Integer getValue(String key, Integer defaultValue) {
		String rawvalue = getStringValue(key);
		if (rawvalue != null) {
			try {
				return Integer.parseInt(rawvalue);
			} catch (NumberFormatException e) {
				removeProperty(key);
			}
		}
		return null;
	}
	
	@Override
	public void setProperty(String key, String value) {
		properies.setProperty(substitutePackageSelector(key), value);
	}
	
	@Override
	public void removeProperty(String key) {
		properies.remove(substitutePackageSelector(key));
	}
	
	@Override
	public String getPackageSelector() {
		return packageSelector;
	}
	
	@Override
	public void setPackageSelector(String packageSelector) {
		this.packageSelector = packageSelector;
	}
	
	public String getPropertiesfile() {
		return propertiesfile;
	}

	public void setPropertiesfile(String propertiesfile) {
		this.propertiesfile = propertiesfile;
	}
}
