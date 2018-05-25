package com.alibaba.logistics.param;

import lombok.Data;

@Data
public class AlibabaLogisticsOpenPlatformTraceNode {

    private String action;

    /**
     * @return 动作
     */
    public String getAction() {
        return action;
    }

    /**
     * 设置动作     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setAction(String action) {
        this.action = action;
    }

    private String areaCode;

    /**
     * @return 地区编码
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * 设置地区编码     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    private String encrypt;

    /**
     * @return 
     */
    public String getEncrypt() {
        return encrypt;
    }

    /**
     * 设置     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

    private String acceptTime;

    /**
     * @return 流转节点的时间
     */
    public String getAcceptTime() {
        return acceptTime;
    }

    /**
     * 设置流转节点的时间     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    private String remark;

    /**
     * @return 备注，如：在浙江浦江县公司进行下级地点扫描，即将发往：广东深圳公司
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注，如：在浙江浦江县公司进行下级地点扫描，即将发往：广东深圳公司     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    private String facilityType;

    /**
     * @return 
     */
    public String getFacilityType() {
        return facilityType;
    }

    /**
     * 设置     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    private String facilityNo;

    /**
     * @return 
     */
    public String getFacilityNo() {
        return facilityNo;
    }

    /**
     * 设置     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setFacilityNo(String facilityNo) {
        this.facilityNo = facilityNo;
    }

    private String facilityName;

    /**
     * @return 
     */
    public String getFacilityName() {
        return facilityName;
    }

    /**
     * 设置     *
     * 参数示例：<pre></pre>     
     * 此参数必填
     */
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

}
