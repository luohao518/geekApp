package com.alibaba.trade.param;

import java.util.*;

public class AlibabaOrderRateDetail {

    private Integer starLevel;

    /**
     * @return 评价星级
     */
    public Integer getStarLevel() {
        return starLevel;
    }

    /**
     * 设置评价星级     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setStarLevel(Integer starLevel) {
        this.starLevel = starLevel;
    }

    private String content;

    /**
     * @return 评价详情
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置评价详情     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setContent(String content) {
        this.content = content;
    }

    private String receiverNick;

    /**
     * @return 收到评价的用户昵称
     */
    public String getReceiverNick() {
        return receiverNick;
    }

    /**
     * 设置收到评价的用户昵称     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setReceiverNick(String receiverNick) {
        this.receiverNick = receiverNick;
    }

    private String posterNick;

    /**
     * @return 发送评价的用户昵称
     */
    public String getPosterNick() {
        return posterNick;
    }

    /**
     * 设置发送评价的用户昵称     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setPosterNick(String posterNick) {
        this.posterNick = posterNick;
    }

    private Date publishTime;

    /**
     * @return 评价上线时间
     */
    public Date getPublishTime() {
        return publishTime;
    }

    /**
     * 设置评价上线时间     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

}
