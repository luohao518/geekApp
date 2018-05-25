package com.alibaba.logistics.param;

import lombok.Data;

@Data
public class AlibabaLogisticsOpenPlatformLogisticsStep {

    private String acceptTime;

    /**
     * @return 物流跟踪单该步骤的时间
     */
    public String getAcceptTime() {
        return acceptTime;
    }

    /**
     * 设置物流跟踪单该步骤的时间     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    private String remark;

    /**
     * @return 备注，如：“在浙江浦江县公司进行下级地点扫描，即将发往：广东深圳公司”
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注，如：“在浙江浦江县公司进行下级地点扫描，即将发往：广东深圳公司”     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
