package com.alibaba.logistics.param;

import com.alibaba.ocean.rawsdk.client.APIId;
import com.alibaba.ocean.rawsdk.common.AbstractAPIRequest;
import lombok.Data;

@Data
public class AlibabaTradeGetLogisticsTraceInfoBuyerViewParam extends AbstractAPIRequest<AlibabaTradeGetLogisticsTraceInfoBuyerViewResult> {

    public AlibabaTradeGetLogisticsTraceInfoBuyerViewParam() {
        super();
        oceanApiId = new APIId("com.alibaba.logistics", "alibaba.trade.getLogisticsTraceInfo.buyerView", 1);
    }

    private String logisticsId;

    /**
     * @return 该订单下的物流编号
     */
    public String getLogisticsId() {
        return logisticsId;
    }

    /**
     * 设置该订单下的物流编号     *
     * 参数示例：<pre>AL8234243</pre>     
     * 此参数必填
     */
    public void setLogisticsId(String logisticsId) {
        this.logisticsId = logisticsId;
    }

    private Long orderId;

    /**
     * @return 订单号
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置订单号     *
     * 参数示例：<pre>13342343</pre>     
     * 此参数必填
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    private String webSite;

    /**
     * @return 是1688业务还是icbu业务
     */
    public String getWebSite() {
        return webSite;
    }

    /**
     * 设置是1688业务还是icbu业务     *
     * 参数示例：<pre>1688或者alibaba</pre>     
     * 此参数必填
     */
    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

}
