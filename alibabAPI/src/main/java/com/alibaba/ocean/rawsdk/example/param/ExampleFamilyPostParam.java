package com.alibaba.ocean.rawsdk.example.param;

import com.alibaba.ocean.rawsdk.common.AbstractAPIRequest;

public class ExampleFamilyPostParam extends AbstractAPIRequest<ExampleFamilyPostResult> {

	private ExampleFamily family;

	/**
	 * @return 上传Family对象信息
	 */
	public ExampleFamily getFamily() {
		return family;
	}

	/**
	 * 设置上传Family对象信息 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setFamily(ExampleFamily family) {
		this.family = family;
	}

	private String comments;

	/**
	 * @return 备注信息
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * 设置备注信息 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	private byte[] houseImg;

	/**
	 * @return 房屋信息
	 */
	public byte[] getHouseImg() {
		return houseImg;
	}

	/**
	 * 设置房屋信息 * 参数示例：
	 * 
	 * <pre></pre>
	 * 
	 * 此参数必填
	 */
	public void setHouseImg(byte[] houseImg) {
		this.houseImg = houseImg;
	}

}
