package com.my.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.entity.Dog;

public final class JsonUtil {
	private static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

	private static ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mapper.setDateFormat(dateFormat);
		
		mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
		mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		
		//mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}

	public static <T> T fromJson(String jsonString, Class<T> clazz) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			return mapper.readValue(jsonString, clazz);
		}
		catch (IOException e) {
			logger.error("parse json string error:" + jsonString, e);
			return null;
		}
	}

	public static <T> T fromJson(String jsonString, Class<?> parametrized, Class<?>... parameterClasses) {
		if (StringUtils.isEmpty(jsonString)) {
			return null;
		}

		try {
			JavaType javaType = mapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
			return mapper.readValue(jsonString, javaType);
		}
		catch (IOException e) {
			logger.warn("parse json string error:" + jsonString, e);
			return null;
		}
	}

	public static String toJson(Object object) {

		try {
			return mapper.writeValueAsString(object);
		}
		catch (IOException e) {
			logger.error("write to json string error:" + object, e);
			return null;
		}
	}

    public static String toFormatJson(Object object) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        }
        catch (IOException e) {
            logger.error("write to json string error:" + object, e);
            return null;
        }
    }

	public static String fixJsonFormat(String data) {
		try {
			if (data.indexOf("'") != -1 && data.indexOf("\"") == -1) {
				return data.replaceAll("'", "\"");
			}
			return data;
		}
		catch (Exception e) {
			return data;
		}
	}
	
	public static boolean isJsonString(String data) {
		try {
			mapper.readValue(data, Object.class);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public static void main(String[] args) {
		String jsonString = "{\"name\":\"doggy\",\"age\":0,\"birth\":\"2016-2-3 11:05:06\"}";
		Dog map = JsonUtil.fromJson(jsonString, Dog.class);
		System.out.println(map);
		
		System.out.println(JsonUtil.toJson(map));
		System.out.println(JsonUtil.toFormatJson(map));
	}
}
