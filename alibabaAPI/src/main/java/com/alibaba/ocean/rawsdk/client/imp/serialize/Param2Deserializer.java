/**
 * 
 */
package com.alibaba.ocean.rawsdk.client.imp.serialize;

import com.alibaba.ocean.rawsdk.client.policy.Protocol;

/**
 * @author hongbang.hb
 *
 */
public class Param2Deserializer extends Json2Deserializer {

	@Override
	public String supportedContentType() {
		return Protocol.param2.name();
	}

}
