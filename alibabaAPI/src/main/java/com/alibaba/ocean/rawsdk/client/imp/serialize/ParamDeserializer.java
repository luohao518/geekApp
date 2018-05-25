/**
 * 
 */
package com.alibaba.ocean.rawsdk.client.imp.serialize;

import com.alibaba.ocean.rawsdk.client.policy.Protocol;

/**
 * @author hongbang.hb
 *
 */
public class ParamDeserializer extends JsonDeserializer {

	@Override
	public String supportedContentType() {
		return Protocol.param.name();
	}

}
