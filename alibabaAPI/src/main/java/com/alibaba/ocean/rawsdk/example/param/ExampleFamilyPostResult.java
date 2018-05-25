package com.alibaba.ocean.rawsdk.example.param;

public class ExampleFamilyPostResult {

	private ExampleFamily result;

	/**
	 * @return 返回的接听信息
	 */
	public ExampleFamily getResult() {
		return result;
	}

	/**
	 * 设置返回的接听信息 *
	 * 
	 * 此参数必填
	 */
	public void setResult(ExampleFamily result) {
		this.result = result;
	}

	private String resultDesc;

	/**
	 * @return 返回结果描述
	 */
	public String getResultDesc() {
		return resultDesc;
	}

	/**
	 * 设置返回结果描述 *
	 * 
	 * 此参数必填
	 */
	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

}
