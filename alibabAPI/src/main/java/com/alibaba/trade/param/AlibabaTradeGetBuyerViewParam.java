package com.alibaba.trade.param;

import com.alibaba.ocean.rawsdk.client.APIId;
import com.alibaba.ocean.rawsdk.common.AbstractAPIRequest;

public class AlibabaTradeGetBuyerViewParam extends AbstractAPIRequest<AlibabaTradeGetBuyerViewResult> {

    public AlibabaTradeGetBuyerViewParam() {
        super();
        oceanApiId = new APIId("com.alibaba.trade", "alibaba.trade.get.buyerView", 1);
    }

    private String webSite;

    /**
     * @return 站点信息，指定调用的API是属于国际站（alibaba）还是1688网站（1688）
     */
    public String getWebSite() {
        return webSite;
    }

    /**
     * 设置站点信息，指定调用的API是属于国际站（alibaba）还是1688网站（1688）     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    private Long orderId;

    /**
     * @return 交易的订单id
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置交易的订单id     *
     * 参数示例：<pre>123456</pre>     
     * 此参数必填
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    private String includeFields;

    /**
     * @return 查询结果中包含的域，GuaranteesTerms：保障条款，NativeLogistics：物流信息，RateDetail：评价详情，OrderInvoice：发票信息。默认返回GuaranteesTerms、NativeLogistics、OrderInvoice。
     */
    public String getIncludeFields() {
        return includeFields;
    }

    /**
     * 设置查询结果中包含的域，GuaranteesTerms：保障条款，NativeLogistics：物流信息，RateDetail：评价详情，OrderInvoice：发票信息。默认返回GuaranteesTerms、NativeLogistics、OrderInvoice。     *
     * 参数示例：<pre>GuaranteesTerms,NativeLogistics,RateDetail,OrderInvoice</pre>     
     * 此参数必填
     */
    public void setIncludeFields(String includeFields) {
        this.includeFields = includeFields;
    }

    private String[] attributeKeys;

    /**
     * @return 垂直表中的attributeKeys
     */
    public String[] getAttributeKeys() {
        return attributeKeys;
    }

    /**
     * 设置垂直表中的attributeKeys     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setAttributeKeys(String[] attributeKeys) {
        this.attributeKeys = attributeKeys;
    }

}
