package com.alibaba.trade.param;

public class AlibabaOrderDetailCaigouQuoteInfo {

    private String productQuoteName;

    /**
     * @return 供应单项的名称
     */
    public String getProductQuoteName() {
        return productQuoteName;
    }

    /**
     * 设置供应单项的名称     *
     * 参数示例：<pre>物料01</pre>     
     * 此参数必填
     */
    public void setProductQuoteName(String productQuoteName) {
        this.productQuoteName = productQuoteName;
    }

    private Long price;

    /**
     * @return 价格，单位：分
     */
    public Long getPrice() {
        return price;
    }

    /**
     * 设置价格，单位：分     *
     * 参数示例：<pre>100</pre>     
     * 此参数必填
     */
    public void setPrice(Long price) {
        this.price = price;
    }

    private Double count;

    /**
     * @return 购买数量
     */
    public Double getCount() {
        return count;
    }

    /**
     * 设置购买数量     *
     * 参数示例：<pre>10</pre>     
     * 此参数必填
     */
    public void setCount(Double count) {
        this.count = count;
    }

}
