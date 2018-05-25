/**
 * 
 */
package com.alibaba.ocean.rawsdk.client;

import com.alibaba.ocean.rawsdk.client.serialize.DeSerializerListener;
import com.alibaba.ocean.rawsdk.client.serialize.SerializerListener;

/**
 * @author hongbang.hb
 *
 */
public interface SDKListener {
	
	public void register(SerializerListener serializerListener);

	public void register(DeSerializerListener deSerializerListener);
	
}
