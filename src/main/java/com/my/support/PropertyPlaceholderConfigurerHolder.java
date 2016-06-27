package com.my.support;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.my.util.PropertiesUtil;

public class PropertyPlaceholderConfigurerHolder extends PropertyPlaceholderConfigurer {

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
		PropertiesUtil.setProperties(props);
		super.processProperties(beanFactoryToProcess, props);
	}

}
