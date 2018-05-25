package com.alibaba.ocean.rawsdk.client.util;

import java.io.IOException;
import java.util.Map;

import com.alibaba.ocean.rawsdk.client.exception.OceanException;
import com.alibaba.ocean.rawsdk.client.policy.Protocol;
import com.alibaba.ocean.rawsdk.util.GenericsUtil;

/**
 * Build <code>Exception</code> for all kinds of
 * {@link Protocol}, base on the exption
 * string which got from <code>Reader</code>
 * 
 * @author xiaoning.qxn
 */
public class ExceptionParser {

	/**
	 * for Protocol.Json2
	 * 
	 * @param exption
	 * @return the new exception
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static Throwable buildException4Json2(Map exption) {
		Map<String, Object> m = exption;

		String errorCodeStr = (String) m.get("error_code");
		String errorMesage = (String) m.get("error_message");

		if (errorCodeStr == null) {
			errorCodeStr = (String) m.get("errorCode");
		}

		if (errorMesage == null) {
			errorMesage = (String) m.get("errorMessage");
		}

		// if null ,then just return the result
		if (GenericsUtil.isBlank(errorMesage)) {
			errorMesage = "Unknow message defined in response.";
		}
		return buildException(errorCodeStr, errorMesage);
	}

	@SuppressWarnings("unchecked")
	public static Throwable buildException4OAuth2(Object exption) {
		Map<String, Object> m = (Map<String, Object>) exption;
		int errorCode = 401;
		String errorMesage = (String) m.get("error_description");
		return buildException(errorCode, errorMesage);
	}

	private static Throwable buildException(int errorCode, String errorMesage) {

		return new OceanException(String.valueOf(errorCode), errorMesage);
	}

	private static Throwable buildException(String errorCode, String errorMesage) {
		return new OceanException(errorCode, errorMesage);
	}
}
