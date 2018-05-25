package com.alibaba.trade.param;

import lombok.Data;

@Data
public class AlibabaOpenplatformTradeModelTradeInfo {

    private AlibabaOpenplatformTradeModelGuaranteeTermsInfo guaranteesTerms;

    /**
     * @return 保障条款
     */
    public AlibabaOpenplatformTradeModelGuaranteeTermsInfo getGuaranteesTerms() {
        return guaranteesTerms;
    }

    /**
     * 设置保障条款     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setGuaranteesTerms(AlibabaOpenplatformTradeModelGuaranteeTermsInfo guaranteesTerms) {
        this.guaranteesTerms = guaranteesTerms;
    }

    private AlibabaOpenplatformTradeModelInternationalLogisticsInfo internationalLogistics;

    /**
     * @return 国际物流
     */
    public AlibabaOpenplatformTradeModelInternationalLogisticsInfo getInternationalLogistics() {
        return internationalLogistics;
    }

    /**
     * 设置国际物流     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setInternationalLogistics(AlibabaOpenplatformTradeModelInternationalLogisticsInfo internationalLogistics) {
        this.internationalLogistics = internationalLogistics;
    }

    private AlibabaOpenplatformTradeModelNativeLogisticsInfo nativeLogistics;

    /**
     * @return 国内物流
     */
    public AlibabaOpenplatformTradeModelNativeLogisticsInfo getNativeLogistics() {
        return nativeLogistics;
    }

    /**
     * 设置国内物流     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setNativeLogistics(AlibabaOpenplatformTradeModelNativeLogisticsInfo nativeLogistics) {
        this.nativeLogistics = nativeLogistics;
    }

    private AlibabaOpenplatformTradeModelProductItemInfo[] productItems;

    /**
     * @return 商品条目信息
     */
    public AlibabaOpenplatformTradeModelProductItemInfo[] getProductItems() {
        return productItems;
    }

    /**
     * 设置商品条目信息     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setProductItems(AlibabaOpenplatformTradeModelProductItemInfo[] productItems) {
        this.productItems = productItems;
    }

    private AlibabaOpenplatformTradeModelTradeTermsInfo[] tradeTerms;

    /**
     * @return 交易条款
     */
    public AlibabaOpenplatformTradeModelTradeTermsInfo[] getTradeTerms() {
        return tradeTerms;
    }

    /**
     * 设置交易条款     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setTradeTerms(AlibabaOpenplatformTradeModelTradeTermsInfo[] tradeTerms) {
        this.tradeTerms = tradeTerms;
    }

    private AlibabaOpenplatformTradeKeyValuePair[] extAttributes;

    /**
     * @return 订单扩展属性
     */
    public AlibabaOpenplatformTradeKeyValuePair[] getExtAttributes() {
        return extAttributes;
    }

    /**
     * 设置订单扩展属性     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setExtAttributes(AlibabaOpenplatformTradeKeyValuePair[] extAttributes) {
        this.extAttributes = extAttributes;
    }

    private AlibabaTradeOrderRateInfo orderRateInfo;

    /**
     * @return 订单评价信息
     */
    public AlibabaTradeOrderRateInfo getOrderRateInfo() {
        return orderRateInfo;
    }

    /**
     * 设置订单评价信息     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setOrderRateInfo(AlibabaTradeOrderRateInfo orderRateInfo) {
        this.orderRateInfo = orderRateInfo;
    }

    private AlibabaInvoiceOrderInvoiceModel orderInvoiceInfo;

    /**
     * @return 发票信息
     */
    public AlibabaInvoiceOrderInvoiceModel getOrderInvoiceInfo() {
        return orderInvoiceInfo;
    }

    /**
     * 设置发票信息     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setOrderInvoiceInfo(AlibabaInvoiceOrderInvoiceModel orderInvoiceInfo) {
        this.orderInvoiceInfo = orderInvoiceInfo;
    }

    private AlibabaTradeCustoms customs;

    /**
     * @return 跨境报关信息
     */
    public AlibabaTradeCustoms getCustoms() {
        return customs;
    }

    /**
     * 设置跨境报关信息     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setCustoms(AlibabaTradeCustoms customs) {
        this.customs = customs;
    }

    private AlibabaTradeOverseasExtraAddress overseasExtraAddress;

    /**
     * @return 跨境地址扩展信息
     */
    public AlibabaTradeOverseasExtraAddress getOverseasExtraAddress() {
        return overseasExtraAddress;
    }

    /**
     * 设置跨境地址扩展信息     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setOverseasExtraAddress(AlibabaTradeOverseasExtraAddress overseasExtraAddress) {
        this.overseasExtraAddress = overseasExtraAddress;
    }

    private AlibabaOpenplatformTradeModelOrderBaseInfo baseInfo;

    /**
     * @return 订单基础信息
     */
    public AlibabaOpenplatformTradeModelOrderBaseInfo getBaseInfo() {
        return baseInfo;
    }

    /**
     * 设置订单基础信息     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setBaseInfo(AlibabaOpenplatformTradeModelOrderBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    private AlibabaOrderBizInfo orderBizInfo;

    /**
     * @return 订单业务信息
     */
    public AlibabaOrderBizInfo getOrderBizInfo() {
        return orderBizInfo;
    }

    /**
     * 设置订单业务信息     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setOrderBizInfo(AlibabaOrderBizInfo orderBizInfo) {
        this.orderBizInfo = orderBizInfo;
    }

    private AlibabaOrderDetailCaigouQuoteInfo[] quoteList;

    /**
     * @return 采购单详情列表，为大企业采购订单独有域。
     */
    public AlibabaOrderDetailCaigouQuoteInfo[] getQuoteList() {
        return quoteList;
    }

    /**
     * 设置采购单详情列表，为大企业采购订单独有域。     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setQuoteList(AlibabaOrderDetailCaigouQuoteInfo[] quoteList) {
        this.quoteList = quoteList;
    }

}
