package com.alibaba.ocean.rawsdk.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PropertyUtils {

	private static HashMap<Class, PropertyDescriptor[]> descriptorsCache = new HashMap<Class, PropertyDescriptor[]>();

	public static PropertyDescriptor[] getPropertyDescriptors(Class beanClass) {

		if (beanClass == null) {
			return new PropertyDescriptor[0];
		}

		PropertyDescriptor descriptors[] = null;
		descriptors = (PropertyDescriptor[]) descriptorsCache.get(beanClass);
		if (descriptors != null) {
			return (descriptors);
		}
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(beanClass);
		} catch (IntrospectionException e) {
			return (new PropertyDescriptor[0]);
		}
		descriptors = beanInfo.getPropertyDescriptors();
		if (descriptors == null) {
			descriptors = new PropertyDescriptor[0];
		}
		descriptorsCache.put(beanClass, descriptors);
		return (descriptors);

	}

	public static PropertyDescriptor[] getPropertyDescriptors(Object bean) {
		if (bean == null) {
			return new PropertyDescriptor[0];
		}
		return getPropertyDescriptors(bean.getClass());
	}

	public static Map<String, PropertyDescriptor> getMappedPropertyDescriptors(Object bean) {
		PropertyDescriptor[] dess = getPropertyDescriptors(bean);
		Map<String, PropertyDescriptor> mappedDes = new LinkedHashMap<String, PropertyDescriptor>();
		for (PropertyDescriptor propertyDescriptor : dess) {
			mappedDes.put(propertyDescriptor.getName(), propertyDescriptor);
		}
		return mappedDes;
	}

	public static Map<String, PropertyDescriptor> getMappedPropertyDescriptors(Class beanClass) {
		PropertyDescriptor[] dess = getPropertyDescriptors(beanClass);
		Map<String, PropertyDescriptor> mappedDes = new LinkedHashMap<String, PropertyDescriptor>();
		for (PropertyDescriptor propertyDescriptor : dess) {
			mappedDes.put(propertyDescriptor.getName(), propertyDescriptor);
		}
		return mappedDes;
	}
}
