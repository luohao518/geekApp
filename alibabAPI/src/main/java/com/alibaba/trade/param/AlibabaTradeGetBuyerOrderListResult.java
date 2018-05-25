package com.alibaba.trade.param;

import lombok.Data;

@Data
public class AlibabaTradeGetBuyerOrderListResult {

    private AlibabaOpenplatformTradeModelTradeInfo[] result;

    /**
     * @return 查询返回列表
     */
    public AlibabaOpenplatformTradeModelTradeInfo[] getResult() {
        return result;
    }

    /**
     * 设置查询返回列表     *
          
     * 此参数必填
     */
    public void setResult(AlibabaOpenplatformTradeModelTradeInfo[] result) {
        this.result = result;
    }

    private String errorCode;

    /**
     * @return 
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 设置     *
          
     * 此参数必填
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    private String errorMessage;

    /**
     * @return 
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 设置     *
          
     * 此参数必填
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private Long totalRecord;

    /**
     * @return 总记录数
     */
    public Long getTotalRecord() {
        return totalRecord;
    }

    /**
     * 设置总记录数     *
          
     * 此参数必填
     */
    public void setTotalRecord(Long totalRecord) {
        this.totalRecord = totalRecord;
    }

}
