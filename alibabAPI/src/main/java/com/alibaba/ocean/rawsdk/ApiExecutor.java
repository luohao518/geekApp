package com.alibaba.ocean.rawsdk;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.ocean.rawsdk.client.SDKListener;
import com.alibaba.ocean.rawsdk.client.SyncAPIClient;
import com.alibaba.ocean.rawsdk.client.serialize.DeSerializerListener;
import com.alibaba.ocean.rawsdk.client.APIId;
import com.alibaba.ocean.rawsdk.client.AlibabaClientFactory;
import com.alibaba.ocean.rawsdk.client.Request;
import com.alibaba.ocean.rawsdk.client.entity.AuthorizationToken;
import com.alibaba.ocean.rawsdk.client.entity.AuthorizationTokenStore;
import com.alibaba.ocean.rawsdk.client.entity.DefaultAuthorizationTokenStore;
import com.alibaba.ocean.rawsdk.client.exception.OceanException;
import com.alibaba.ocean.rawsdk.client.policy.ClientPolicy;
import com.alibaba.ocean.rawsdk.client.policy.RequestPolicy;
import com.alibaba.ocean.rawsdk.client.serialize.SerializerListener;
import com.alibaba.ocean.rawsdk.common.AbstractAPIRequest;
import com.alibaba.ocean.rawsdk.common.SDKResult;

/**
 * The API facade.
 */
public final class ApiExecutor implements SDKListener {

	private String serverHost = "gw.open.1688.com";
	private int httpPort = 80;
	private int httpsPort = 443;
	private String appKey;
	private String secKey;

	private AuthorizationTokenStore authorizationTokenStore;

	private Map<Class<? extends SerializerListener>, SerializerListener> serializerListeners = new LinkedHashMap<Class<? extends SerializerListener>, SerializerListener>();
	private Map<Class<? extends DeSerializerListener>, DeSerializerListener> deSerializerListeners = new LinkedHashMap<Class<? extends DeSerializerListener>, DeSerializerListener>();

	public ApiExecutor(String appKey, String secKey) {
		super();
		this.appKey = appKey;
		this.secKey = secKey;
	}

	public ApiExecutor(String serverHost, int httpPort, int httpsPort, String appKey, String secKey) {
		super();
		this.serverHost = serverHost;
		this.httpPort = httpPort;
		this.httpsPort = httpsPort;
		this.appKey = appKey;
		this.secKey = secKey;
	}

	public void register(SerializerListener serializerListener) {
		serializerListeners.put(serializerListener.getClass(), serializerListener);
	}

	public void register(DeSerializerListener deSerializerListener) {
		deSerializerListeners.put(deSerializerListener.getClass(), deSerializerListener);
	}

	private SyncAPIClient getAPIClient() {
		ClientPolicy clientPolicy = new ClientPolicy(serverHost);
		clientPolicy.setHttpPort(httpPort);
		clientPolicy.setHttpsPort(httpsPort);
		if (appKey != null) {
			clientPolicy.setAppKey(appKey);
		}
		if (secKey != null) {
			clientPolicy.setSigningKey(secKey);
		}
		if (authorizationTokenStore == null) {
			authorizationTokenStore = new DefaultAuthorizationTokenStore();
		}
		SyncAPIClient syncAPIClient = new AlibabaClientFactory().createAPIClient(clientPolicy, authorizationTokenStore);
		return syncAPIClient;
	}

	/**
	 * 
	 * 
	 * @param code
	 * 
	 * @return
	 */
	public final AuthorizationToken getToken(String code) {
		try {
			return getAPIClient().getToken(code);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * refresh the access token with refreshToken
	 * 
	 * @param refreshToken
	 * 
	 * @return access token object.
	 */
	public final AuthorizationToken refreshToken(String refreshToken) {
		try {
			return getAPIClient().refreshToken(refreshToken);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param apiRequest
	 * @return
	 */
	public final <TResponse> SDKResult<TResponse> execute(AbstractAPIRequest<TResponse> apiRequest) {
		RequestPolicy reqPolicy = apiRequest.getOceanRequestPolicy();
		try {
			APIId apiId = apiRequest.getOceanApiId();
			Request req = new Request(apiId.getNamespace(), apiId.getName(), apiId.getVersion());

			req.setRequestEntity(apiRequest);
			TResponse ret = getAPIClient().send(req, apiRequest.getResponseClass(), reqPolicy,
					serializerListeners.values(), deSerializerListeners.values());
			return new SDKResult<TResponse>(ret);
		} catch (OceanException e) {
			return new SDKResult<TResponse>(e.getErrorCode(), e.getErrorMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param apiRequest
	 * @return
	 */
	public final <TResponse> SDKResult<TResponse> execute(AbstractAPIRequest<TResponse> apiRequest, String accessToken) {
		RequestPolicy reqPolicy = apiRequest.getOceanRequestPolicy();
		try {
			APIId apiId = apiRequest.getOceanApiId();
			Request req = new Request(apiId.getNamespace(), apiId.getName(), apiId.getVersion());

			req.setRequestEntity(apiRequest);
			req.setAccessToken(accessToken);
			TResponse ret = getAPIClient().send(req, apiRequest.getResponseClass(), reqPolicy,
					serializerListeners.values(), deSerializerListeners.values());
			return new SDKResult<TResponse>(ret);
		} catch (OceanException e) {
			return new SDKResult<TResponse>(e.getErrorCode(), e.getErrorMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
