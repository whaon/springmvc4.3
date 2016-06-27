package com.my.util;

import java.util.Properties;

/**
 * @author 童浩
 * @date 2016年3月21日
 * @version 0.1
 * @see com.mileweb.dmp.deploy.support.PropertyPlaceholderConfigurerHolder
 */
public class PropertiesUtil {
	
	private static Properties props = new Properties();

	public static void setProperties(Properties props) {
		PropertiesUtil.props = props;
	}
	
	public static String get(String key) {
		return props.getProperty(key);
	}

	public static String get(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}
	
}
