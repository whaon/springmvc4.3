package com.my.util;

/**
 * @(#)JsonUtil.java 2013-7-11
 * 
 * Copyright 2000-2013 by ChinanetCenter Corporation.
 *
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * ChinanetCenter Corporation ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with ChinanetCenter.
 */

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * mapper for json <-> java bean 
 * 
 * @author 刘圳
 * @since 2013-7-11
 */
public abstract class JsonUtil {

	/**
	 * fastjosn的ClassSerializer, 在输出null值的时没有判断, 会有问题
	 * @author sinlang
	 * @date 2014年7月2日
	 */
	public static class ClassSerializer implements ObjectSerializer {

		public final static ClassSerializer instance = new ClassSerializer();

		@SuppressWarnings("rawtypes")
		public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType)
				throws IOException {
			SerializeWriter out = serializer.getWriter();

			if (object == null) {
				out.writeNull();
			}
			else {
				Class clazz = (Class) object;
				out.writeString(clazz.getName());
			}
		}
	}

	/**
	 * json序列化成字符串
	 * @param message
	 * @param prettyFormat 是否使用格式化
	 * @param withType 是否写入类型
	 * @return
	 */
	public static String toText(Object message, boolean prettyFormat, boolean withType) {
		SerializeWriter out = new SerializeWriter();
		try {
			JSONSerializer serializer = new JSONSerializer(out);
			serializer.getMapping().getGlobalInstance().put(Class.class, ClassSerializer.instance);
			serializer.config(SerializerFeature.WriteMapNullValue, true);
			if (prettyFormat) {
				serializer.config(SerializerFeature.PrettyFormat, true);
			}
			if (withType) {
				serializer.config(SerializerFeature.WriteClassName, true);
			}
			serializer.write(message);

			return out.toString();
		}
		finally {
			out.close();
		}
	}

	/**
	 * 将json转换成list数组，数组内元素类型相同，需提供数组元素类型
	 * 
	 * @param text json字符串
	 * @param clazz 数组元素类型
	 * @return
	 */
	public static <T> List<T> parseArray(String text, Class<T> clazz) {
		return JSON.parseArray(text, clazz);
	}

	/**
	 * 将json转换成list数组，数组内元素类型可以不同, 必须分别由types指定
	 * @param input
	 * @param types
	 * @return
	 */
	public List<?> parseArray(String input, Class<?>[] types) {
		return JSON.parseArray(input, types);
	}

	/**
	 * 把message对象转换成json字符串
	 * @param message
	 * @return
	 */
	public static String toText(Object message) {
		return toText(message, false, false);
	}

	/**
	 * 把message对象转换成json字符串
	 * @param message
	 * @return
	 */
	public static String toPrettyText(Object message) {

		return toText(message, true, false);
	}

	/**
	 * 把message对象转换成json字符串, 对象带有java类名
	 * @param message
	 * @return
	 */
	public static String toTextWithType(Object message) {
		return toText(message, false, true);
	}

	public static String toTextWithTypePretty(Object message) {
		return toText(message, true, true);
	}

	/**
	 * 把字符串转换成java对象
	 * @param input
	 * @return
	 */
	public static Object parse(String input) {
		return JSON.parse(input);
	}

	/**
	 * 把字符串转换成java对象, 并指定放回值对象
	 * @param input
	 * @return
	 */
	public static <T> T parse(String input, Class<T> type) {
		return JSON.parseObject(input, type);
	}

	public static <T> T clone(T object, Class<T> type) {
		String text = toTextWithType(object);
		return (T) parse(text, type);
	}

	public static <T> T clone(T object) {
		String text = toTextWithType(object);
		return (T) parse(text);
	}
}

