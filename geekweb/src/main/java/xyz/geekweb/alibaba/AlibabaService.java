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

/*    public static final String APP_KEY = "7031967";
    public static final String SEC_KEY = "z2tB0cavGIL";
    public static final String ACCESS_TOKEN = "80a28982-ceb7-4014-ab56-651acdd013bc";*/

    //6366071 hksq0mhABL 27c77863-bc0c-41d3-b066-71f62ce11799
    //5756323 eCq5mnbymF cdb59e10-c23b-4d96-b33e-55a457ae43e3
    //6734282 icjtjI95zt 62bde37c-555e-47e4-9293-fcc100666e97
    //7031967 z2tB0cavGIL 80a28982-ceb7-4014-ab56-651acdd013bc
    //9601728 J84jiRxcb87h 2b40c3ca-9a98-4987-ba7a-5bc96ed985df

    private static final ApiExecutor apiExecutor = new ApiExecutor(APP_KEY, SEC_KEY);

    public SDKResult<AlibabaTradeGetLogisticsTraceInfoBuyerViewResult> getLogisticsTraceInfo(long orderId) {

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
        return apiExecutor.execute(param, ACCESS_TOKEN);
    }

    public SDKResult<AlibabaTradeGetBuyerOrderListResult> getBuyerOrderList() {

        APIId oceanApiId = new APIId("com.alibaba.trade", "alibaba.trade.getBuyerOrderList", 1);
        AlibabaTradeGetBuyerOrderListParam param = new AlibabaTradeGetBuyerOrderListParam();
        param.setOceanApiId(oceanApiId);
        RequestPolicy oceanRequestPolicy = new RequestPolicy();
        oceanRequestPolicy.setHttpMethod(HttpMethodPolicy.POST).setNeedAuthorization(false)
                .setRequestSendTimestamp(false).setUseHttps(false).setUseSignture(true).setAccessPrivateApi(false)
                .setDateFormat(DateUtil.SIMPLE_DATE_FORMAT_STR);
        param.setOceanRequestPolicy(oceanRequestPolicy);

        // Calling and get the result.
        return apiExecutor.execute(param, ACCESS_TOKEN);
    }

    public SDKResult<AlibabaTradeGetBuyerViewResult> getBuyerViewTrade(long orderId) {

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
        return apiExecutor.execute(param, ACCESS_TOKEN);
    }
}
