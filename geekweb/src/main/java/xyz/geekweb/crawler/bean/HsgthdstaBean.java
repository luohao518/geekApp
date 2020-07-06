package xyz.geekweb.crawler.bean;

/**
 * 沪深港通每日持股统计
 */

import lombok.Data;


@Data
public class HsgthdstaBean {

    private String HDDATE;
    private String HKCODE;
    private String SCODE;
    private String SNAME;
    private long SHAREHOLDSUM;
    private double SHARESRATE;
    private double CLOSEPRICE;
    private double ZDF;
    private double SHAREHOLDPRICE;
    private double SHAREHOLDPRICEONE;
    private double SHAREHOLDPRICEFIVE;
    private double SHAREHOLDPRICETEN;
    private String MARKET;
    private String ShareHoldSumChg;
    private double Zb;
    private double Zzb;


}
