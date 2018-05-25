package com.alibaba.trade.param;

public class AlibabaTradeGetBuyerViewResult {

    private AlibabaOpenplatformTradeModelTradeInfo result;

    /**
     * @return 订单详情信息
     */
    public AlibabaOpenplatformTradeModelTradeInfo getResult() {
        return result;
    }

    /**
     * 设置订单详情信息     *
          
     * 此参数必填
     */
    public void setResult(AlibabaOpenplatformTradeModelTradeInfo result) {
        this.result = result;
    }

    private String errorCode;

    /**
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 设置错误代码     *
          
     * 此参数必填
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    private String errorMessage;

    /**
     * @return 错误描述
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * 设置错误描述     *
          
     * 此参数必填
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
