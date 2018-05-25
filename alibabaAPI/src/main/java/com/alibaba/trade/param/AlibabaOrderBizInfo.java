package com.alibaba.trade.param;

public class AlibabaOrderBizInfo {

    private Boolean odsCyd;

    /**
     * @return 是否采源宝订单
     */
    public Boolean getOdsCyd() {
        return odsCyd;
    }

    /**
     * 设置是否采源宝订单     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setOdsCyd(Boolean odsCyd) {
        this.odsCyd = odsCyd;
    }

    private String accountPeriodTime;

    /**
     * @return 账期交易订单的到账时间
     */
    public String getAccountPeriodTime() {
        return accountPeriodTime;
    }

    /**
     * 设置账期交易订单的到账时间     *
     * 参数示例：<pre>yyyy-MM-dd HH:mm:ss</pre>     
     * 此参数必填
     */
    public void setAccountPeriodTime(String accountPeriodTime) {
        this.accountPeriodTime = accountPeriodTime;
    }

}
