package com.alibaba.trade.param;

public class AlibabaTradeCustomsAttributesInfo {

    private String sku;

    /**
     * @return sku标识
     */
    public String getSku() {
        return sku;
    }

    /**
     * 设置sku标识     *
     * 参数示例：<pre>1234</pre>     
     * 此参数必填
     */
    public void setSku(String sku) {
        this.sku = sku;
    }

    private String cName;

    /**
     * @return 中文名称
     */
    public String getCName() {
        return cName;
    }

    /**
     * 设置中文名称     *
     * 参数示例：<pre>测试</pre>     
     * 此参数必填
     */
    public void setCName(String cName) {
        this.cName = cName;
    }

    private String enName;

    /**
     * @return 英文名称
     */
    public String getEnName() {
        return enName;
    }

    /**
     * 设置英文名称     *
     * 参数示例：<pre>test</pre>     
     * 此参数必填
     */
    public void setEnName(String enName) {
        this.enName = enName;
    }

    private Double amount;

    /**
     * @return 申报价值
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * 设置申报价值     *
     * 参数示例：<pre>3000.0</pre>     
     * 此参数必填
     */
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    private Double quantity;

    /**
     * @return 数量
     */
    public Double getQuantity() {
        return quantity;
    }

    /**
     * 设置数量     *
     * 参数示例：<pre>1.0</pre>     
     * 此参数必填
     */
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    private Double weight;

    /**
     * @return 重量（kg）
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * 设置重量（kg）     *
     * 参数示例：<pre>0.5</pre>     
     * 此参数必填
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    private String currency;

    /**
     * @return 报关币种
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * 设置报关币种     *
     * 参数示例：<pre>CNY</pre>     
     * 此参数必填
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

}
