package com.alibaba.trade.param;

public class AlibabaTradeOverseasExtraAddress {

    private String channelName;

    /**
     * @return 路线名称
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * 设置路线名称     *
     * 参数示例：<pre>欧洲小包</pre>     
     * 此参数必填
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    private String channelId;

    /**
     * @return 路线id
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * 设置路线id     *
     * 参数示例：<pre>1</pre>     
     * 此参数必填
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    private String shippingCompanyId;

    /**
     * @return 货代公司id
     */
    public String getShippingCompanyId() {
        return shippingCompanyId;
    }

    /**
     * 设置货代公司id     *
     * 参数示例：<pre>222</pre>     
     * 此参数必填
     */
    public void setShippingCompanyId(String shippingCompanyId) {
        this.shippingCompanyId = shippingCompanyId;
    }

    private String shippingCompanyName;

    /**
     * @return 货代公司名称
     */
    public String getShippingCompanyName() {
        return shippingCompanyName;
    }

    /**
     * 设置货代公司名称     *
     * 参数示例：<pre>货代公司1</pre>     
     * 此参数必填
     */
    public void setShippingCompanyName(String shippingCompanyName) {
        this.shippingCompanyName = shippingCompanyName;
    }

    private String countryCode;

    /**
     * @return 国家code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * 设置国家code     *
     * 参数示例：<pre>UK</pre>     
     * 此参数必填
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    private String country;

    /**
     * @return 国家
     */
    public String getCountry() {
        return country;
    }

    /**
     * 设置国家     *
     * 参数示例：<pre>英国</pre>     
     * 此参数必填
     */
    public void setCountry(String country) {
        this.country = country;
    }

    private String email;

    /**
     * @return 买家邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置买家邮箱     *
     * 参数示例：<pre>aaa@gmail.com</pre>     
     * 此参数必填
     */
    public void setEmail(String email) {
        this.email = email;
    }

}
