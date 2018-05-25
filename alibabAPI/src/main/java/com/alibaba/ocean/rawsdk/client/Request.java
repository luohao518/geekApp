/**
 * Project: ocean.client.java.basic
 *
 * File Created at 2011-10-17
 * $Id: Request.java 406840 2015-04-13 09:12:42Z hongbang.hb $
 *
 * Copyright 2008 Alibaba.com Croporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.alibaba.ocean.rawsdk.client;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.ocean.rawsdk.client.entity.AuthorizationToken;
import com.alibaba.ocean.rawsdk.common.AbstractAPIRequest;

/**
 * API请求实体
 * <p>
 * 
 * @author jade
 * @author xiaoning.qxn
 */
public class Request {

	public static final String ACCESS_TOKEN = "access_token";

	private APIId apiId;
	private Map<String, Object> addtionalParams = new HashMap<String, Object>();
	private AbstractAPIRequest requestEntity;
	private Map<String, String> attachments;
	private String authCodeKey;
	private String accessToken;
	private AuthorizationToken authToken;

	public Request(String namespace, String name) {
		apiId = new APIId(namespace, name);
	}

	/**
	 * 使用API唯一标示：namespace，name，version构建一个API request
	 * 
	 * @param namespace
	 * @param name
	 * @param version
	 */
	public Request(String namespace, String name, int version) {
		apiId = new APIId(namespace, name, version);
	}

	public Request(APIId apiId) {
		this.apiId = apiId;
	}

	public APIId getApiId() {
		return apiId;
	}

	public Request setAttachment(String name, String value) {
		return this;
	}

	public Map<String, String> getAttachments() {
		if (attachments == null) {
			attachments = new LinkedHashMap<String, String>();
		}
		return attachments;
	}

	public AbstractAPIRequest getRequestEntity() {
		return requestEntity;
	}

	public void setRequestEntity(AbstractAPIRequest requestEntity) {
		this.requestEntity = requestEntity;
	}

	public Map<String, Object> getAddtionalParams() {
		return addtionalParams;
	}

	public void addAddtionalParams(String key, Object param) {
		addtionalParams.put(key, param);
	}

	public String getAuthCodeKey() {
		return authCodeKey;
	}

	public Request setAuthCodeKey(String authCodeKey) {
		this.authCodeKey = authCodeKey;
		return this;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public Request setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		addtionalParams.put(ACCESS_TOKEN, accessToken);
		return this;
	}

	public AuthorizationToken getAuthToken() {
		return authToken;
	}

	public Request setAuthToken(AuthorizationToken authToken) {
		this.authToken = authToken;
		return this;
	}
}
