package com.alibaba.trade.param;

import java.util.*;
import java.math.BigDecimal;

public class AlibabaOpenplatformTradeModelTradeTermsInfo {

    private String payStatus;

    /**
     * @return 支付状态。国际站：WAIT_PAY(未支付),PAYER_PAID(已完成支付),PART_SUCCESS(部分支付成功),PAY_SUCCESS(支付成功),CLOSED(风控关闭),CANCELLED(支付撤销),SUCCESS(成功),FAIL(失败)。
    1688:1(未付款);2(已付款);4(全额退款);6(卖家有收到钱，回款完成) ;7(未创建外部支付单);8 (付款前取消) ; 9(正在支付中);12(账期支付,待到账)
     */
    public String getPayStatus() {
        return payStatus;
    }

    /**
     * 设置支付状态。国际站：WAIT_PAY(未支付),PAYER_PAID(已完成支付),PART_SUCCESS(部分支付成功),PAY_SUCCESS(支付成功),CLOSED(风控关闭),CANCELLED(支付撤销),SUCCESS(成功),FAIL(失败)。
    1688:1(未付款);2(已付款);4(全额退款);6(卖家有收到钱，回款完成) ;7(未创建外部支付单);8 (付款前取消) ; 9(正在支付中);12(账期支付,待到账)     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    private Date payTime;

    /**
     * @return 完成阶段支付时间
     */
    public Date getPayTime() {
        return payTime;
    }

    /**
     * 设置完成阶段支付时间     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    private String payWay;

    /**
     * @return 支付方式。
    国际站：ECL(融资支付),CC(信用卡),TT(线下TT),ACH(echecking支付)。
    1688:1-支付宝,2-网商银行信任付,3-诚e赊,4-银行转账,5-赊销宝,6-电子承兑票据,7-账期支付,8-合并支付渠道,9-无打款,10-零售通赊购,13-支付平台,12-声明付款
     */
    public String getPayWay() {
        return payWay;
    }

    /**
     * 设置支付方式。
    国际站：ECL(融资支付),CC(信用卡),TT(线下TT),ACH(echecking支付)。
    1688:1-支付宝,2-网商银行信任付,3-诚e赊,4-银行转账,5-赊销宝,6-电子承兑票据,7-账期支付,8-合并支付渠道,9-无打款,10-零售通赊购,13-支付平台,12-声明付款     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    private BigDecimal phasAmount;

    /**
     * @return 付款额
     */
    public BigDecimal getPhasAmount() {
        return phasAmount;
    }

    /**
     * 设置付款额     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setPhasAmount(BigDecimal phasAmount) {
        this.phasAmount = phasAmount;
    }

    private Long phase;

    /**
     * @return 阶段单id
     */
    public Long getPhase() {
        return phase;
    }

    /**
     * 设置阶段单id     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setPhase(Long phase) {
        this.phase = phase;
    }

    private String phaseCondition;

    /**
     * @return 阶段条件，1688无此内容
     */
    public String getPhaseCondition() {
        return phaseCondition;
    }

    /**
     * 设置阶段条件，1688无此内容     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setPhaseCondition(String phaseCondition) {
        this.phaseCondition = phaseCondition;
    }

    private String phaseDate;

    /**
     * @return 阶段时间，1688无此内容
     */
    public String getPhaseDate() {
        return phaseDate;
    }

    /**
     * 设置阶段时间，1688无此内容     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setPhaseDate(String phaseDate) {
        this.phaseDate = phaseDate;
    }

    private Boolean cardPay;

    /**
     * @return 是否银行卡支付
     */
    public Boolean getCardPay() {
        return cardPay;
    }

    /**
     * 设置是否银行卡支付     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setCardPay(Boolean cardPay) {
        this.cardPay = cardPay;
    }

    private Boolean expressPay;

    /**
     * @return 是否快捷支付
     */
    public Boolean getExpressPay() {
        return expressPay;
    }

    /**
     * 设置是否快捷支付     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setExpressPay(Boolean expressPay) {
        this.expressPay = expressPay;
    }

}
