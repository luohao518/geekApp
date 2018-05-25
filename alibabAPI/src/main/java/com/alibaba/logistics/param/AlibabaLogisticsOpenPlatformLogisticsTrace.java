package com.alibaba.logistics.param;

import lombok.Data;

@Data
public class AlibabaLogisticsOpenPlatformLogisticsTrace {

    private String logisticsId;

    /**
     * @return 物流编号，如BX110096003841234
     */
    public String getLogisticsId() {
        return logisticsId;
    }

    /**
     * 设置物流编号，如BX110096003841234     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setLogisticsId(String logisticsId) {
        this.logisticsId = logisticsId;
    }

    private Long orderId;

    /**
     * @return 订单编号
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置订单编号     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    private String logisticsBillNo;

    /**
     * @return 物流单编号，如480330616596
     */
    public String getLogisticsBillNo() {
        return logisticsBillNo;
    }

    /**
     * 设置物流单编号，如480330616596     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setLogisticsBillNo(String logisticsBillNo) {
        this.logisticsBillNo = logisticsBillNo;
    }

    private AlibabaLogisticsOpenPlatformLogisticsStep[] logisticsSteps;

    /**
     * @return 物流跟踪步骤
     */
    public AlibabaLogisticsOpenPlatformLogisticsStep[] getLogisticsSteps() {
        return logisticsSteps;
    }

    /**
     * 设置物流跟踪步骤     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setLogisticsSteps(AlibabaLogisticsOpenPlatformLogisticsStep[] logisticsSteps) {
        this.logisticsSteps = logisticsSteps;
    }

    private AlibabaLogisticsOpenPlatformTraceNode[] traceNodeList;

    /**
     * @return 物流周转节点
     */
    public AlibabaLogisticsOpenPlatformTraceNode[] getTraceNodeList() {
        return traceNodeList;
    }

    /**
     * 设置物流周转节点     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setTraceNodeList(AlibabaLogisticsOpenPlatformTraceNode[] traceNodeList) {
        this.traceNodeList = traceNodeList;
    }

}
