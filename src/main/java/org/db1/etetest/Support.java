package org.db1.etetest;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.Nullable;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.google.common.base.Strings;

public class Support {
	private static Support instance;
	public synchronized static Support instance() {
		if (instance == null) {
			instance = new Support();
		}
		return instance;
	}
	private Properties props;
	private ConfigurableEnvironment environment;
	private Support() {
		try {
			File file = new File("./etetest.properties");
			Resource resource = new UrlResource(file.toURI());
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getProperty(String name, @Nullable String defaultValue, boolean throwError) {
		String value = instance().environment.getProperty(name);
		if (Strings.isNullOrEmpty(value)) {
			if (throwError) {
				throw new RuntimeException(String.format("property %s is not defined and the system configuration and its default value is not specified", name));
			} else {
				return defaultValue;
			}
		} else {
			return value;
		}
	}
	public static String getProperty(String name) {
		return getProperty(name, null, true);
	}
	
	public static Integer getIntProperty(String name, Integer defaultValue, boolean throwError) {
		return Integer.valueOf(getProperty(name, defaultValue != null ? defaultValue.toString() : null, throwError));
	}

	public static Integer getIntProperty(String name) {
		return Integer.valueOf(getProperty(name, null, true));
	}

	void setEnvironment(ConfigurableEnvironment environment) {
		this.environment = environment;
		this.environment.getPropertySources().addFirst(new PropertiesPropertySource("etetest", props));
	}
}
