/**
 * 
 */
package com.alibaba.ocean.rawsdk.common;

import lombok.Data;

/**
 * @author hongbang.hb
 *
 */
@Data
public class SDKResult<T> {

	private String errorCode;
	private String errorMessage;

	private T result;

	public SDKResult(String code, String message) {
		super();
		this.errorCode = code;
		this.errorMessage = message;
	}

	public SDKResult(T result) {
		super();
		this.result = result;
	}

	public SDKResult() {
		super();
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
}
