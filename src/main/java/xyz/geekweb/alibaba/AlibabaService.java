/**
 * 
 */
package xyz.geekweb.alibaba;

import com.alibaba.logistics.param.AlibabaTradeGetLogisticsTraceInfoBuyerViewParam;
import com.alibaba.logistics.param.AlibabaTradeGetLogisticsTraceInfoBuyerViewResult;
import com.alibaba.ocean.rawsdk.ApiExecutor;
import com.alibaba.ocean.rawsdk.client.APIId;
import com.alibaba.ocean.rawsdk.client.policy.RequestPolicy;
import com.alibaba.ocean.rawsdk.client.policy.RequestPolicy.HttpMethodPolicy;
import com.alibaba.ocean.rawsdk.common.SDKResult;
import com.alibaba.ocean.rawsdk.util.DateUtil;
import com.alibaba.trade.param.AlibabaTradeGetBuyerOrderListParam;
import com.alibaba.trade.param.AlibabaTradeGetBuyerOrderListResult;
import com.alibaba.trade.param.AlibabaTradeGetBuyerViewParam;
import com.alibaba.trade.param.AlibabaTradeGetBuyerViewResult;
import org.springframework.stereotype.Service;

/**
 * @author lhao
 */
@Service
public class AlibabaService {


	public static final String APP_KEY = "1406054";
	public static final String SEC_KEY = "UVltVpGarKR";
	public static final String ACCESS_TOKEN = "a8e5d00b-5e78-4518-ba25-b4d871928111";

	private static final ApiExecutor apiExecutor = new ApiExecutor(APP_KEY, SEC_KEY);

	public SDKResult<AlibabaTradeGetLogisticsTraceInfoBuyerViewResult>  getLogisticsTraceInfo(long orderId) {

		APIId oceanApiId = new APIId("com.alibaba.logistics", "alibaba.trade.getLogisticsTraceInfo.buyerView", 1);
		AlibabaTradeGetLogisticsTraceInfoBuyerViewParam param = new AlibabaTradeGetLogisticsTraceInfoBuyerViewParam();
		param.setOceanApiId(oceanApiId);
		RequestPolicy oceanRequestPolicy = new RequestPolicy();
		oceanRequestPolicy.setHttpMethod(HttpMethodPolicy.POST).setNeedAuthorization(false)
				.setRequestSendTimestamp(false).setUseHttps(false).setUseSignture(true).setAccessPrivateApi(false)
				.setDateFormat(DateUtil.SIMPLE_DATE_FORMAT_STR);
		param.setOceanRequestPolicy(oceanRequestPolicy);

		param.setOrderId(orderId);
		param.setWebSite("1688");

		// Calling and get the result.
		return apiExecutor.execute(param,ACCESS_TOKEN);
	}

	public SDKResult<AlibabaTradeGetBuyerOrderListResult>  getBuyerOrderList() {

		APIId oceanApiId = new APIId("com.alibaba.trade", "alibaba.trade.getBuyerOrderList", 1);
		AlibabaTradeGetBuyerOrderListParam param = new AlibabaTradeGetBuyerOrderListParam();
		param.setOceanApiId(oceanApiId);
		RequestPolicy oceanRequestPolicy = new RequestPolicy();
		oceanRequestPolicy.setHttpMethod(HttpMethodPolicy.POST).setNeedAuthorization(false)
				.setRequestSendTimestamp(false).setUseHttps(false).setUseSignture(true).setAccessPrivateApi(false)
				.setDateFormat(DateUtil.SIMPLE_DATE_FORMAT_STR);
		param.setOceanRequestPolicy(oceanRequestPolicy);

		// Calling and get the result.
		return apiExecutor.execute(param,ACCESS_TOKEN);
	}

	public SDKResult<AlibabaTradeGetBuyerViewResult>  getBuyerViewTrade(long orderId) {

		APIId oceanApiId = new APIId("com.alibaba.trade", "alibaba.trade.get.buyerView", 1);
		AlibabaTradeGetBuyerViewParam param = new AlibabaTradeGetBuyerViewParam();
		param.setOceanApiId(oceanApiId);
		RequestPolicy oceanRequestPolicy = new RequestPolicy();
		oceanRequestPolicy.setHttpMethod(HttpMethodPolicy.POST).setNeedAuthorization(false)
				.setRequestSendTimestamp(false).setUseHttps(false).setUseSignture(true).setAccessPrivateApi(false)
				.setDateFormat(DateUtil.SIMPLE_DATE_FORMAT_STR);
		param.setOceanRequestPolicy(oceanRequestPolicy);

		param.setOrderId(orderId);
		param.setWebSite("1688");

		// Calling and get the result.
		return apiExecutor.execute(param,ACCESS_TOKEN);
	}
}
