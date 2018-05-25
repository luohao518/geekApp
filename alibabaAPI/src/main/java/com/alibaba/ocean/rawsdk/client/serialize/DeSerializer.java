/**
 * 
 */
package com.alibaba.ocean.rawsdk.client.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import com.alibaba.ocean.rawsdk.client.entity.ResponseWrapper;

/**
 * @author hongbang.hb
 *
 */
public interface DeSerializer {

	/**
	 * 返回该反序列化接口支持的数据协议.
	 * 
	 * @see com.alibaba.openapi.client.policy.Protocol
	 * @return
	 */
	public String supportedContentType();

	/**
	 * 把istream反序列化为一个ResponseWrapper对象
	 * 
	 * @param istream
	 * @param resultType
	 * @param charSet
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public <T> ResponseWrapper<T> deSerialize(InputStream istream, Class<T> resultType, String charSet)
			throws IOException, ParseException;

	/**
	 * 把istream反序列化为一个异常对象
	 * 
	 * @param inputStream
	 * @param statusCode
	 * @param charSet
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public Throwable buildException(InputStream inputStream, int statusCode, String charSet) throws IOException,
			ParseException;

	/**
	 * 
	 * @param listner
	 */
	public void registeDeSerializerListener(DeSerializerListener listner);

}
