package com.alibaba.trade.param;

import java.util.*;

public class AlibabaTradeCustoms {

    private Long id;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置id     *
     * 参数示例：<pre>1</pre>     
     * 此参数必填
     */
    public void setId(Long id) {
        this.id = id;
    }

    private Date gmtCreate;

    /**
     * @return 创建时间
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * 设置创建时间     *
     * 参数示例：<pre>20170806114526000+0800</pre>     
     * 此参数必填
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    private Date gmtModified;

    /**
     * @return 修改时间
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * 设置修改时间     *
     * 参数示例：<pre>20170806114526000+0800</pre>     
     * 此参数必填
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    private Long buyerId;

    /**
     * @return 买家id
     */
    public Long getBuyerId() {
        return buyerId;
    }

    /**
     * 设置买家id     *
     * 参数示例：<pre>123456</pre>     
     * 此参数必填
     */
    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    private String orderId;

    /**
     * @return 主订单id
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * 设置主订单id     *
     * 参数示例：<pre>12312312312312</pre>     
     * 此参数必填
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    private Integer type;

    /**
     * @return 业务数据类型,默认1：报关单
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置业务数据类型,默认1：报关单     *
     * 参数示例：<pre>1</pre>     
     * 此参数必填
     */
    public void setType(Integer type) {
        this.type = type;
    }

    private AlibabaTradeCustomsAttributesInfo[] attributes;

    /**
     * @return 报关信息列表
     */
    public AlibabaTradeCustomsAttributesInfo[] getAttributes() {
        return attributes;
    }

    /**
     * 设置报关信息列表     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setAttributes(AlibabaTradeCustomsAttributesInfo[] attributes) {
        this.attributes = attributes;
    }

}
