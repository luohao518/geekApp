package xyz.geekweb.crawler.bean;

/**
 * 沪深港通每日持股统计
 */

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;


@Data
@Entity
public class HSGTHdStaBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    /**
     * 持股日期
     */
    @Column(nullable = false,columnDefinition="varchar(20) COMMENT '持股日期'")
    @SerializedName(value = "HDDATE")
    private String hdDate;

    @Column(nullable = true)
    @SerializedName(value = "HKCODE")
    private String hkCode;

    /**
     * 股票代码
     */
    @Column(nullable = false,columnDefinition="varchar(20) COMMENT '股票代码'")
    @SerializedName(value = "SCODE")
    private String sCode;

    /**
     * 股票名称
     */
    @Column(nullable = false,columnDefinition="varchar(20) COMMENT '股票名称'")
    @SerializedName(value = "SNAME")
    private String sName;

    /**
     * 持股数量
     */
    @Column(nullable = false,columnDefinition="varchar(20) COMMENT '持股数量'")
    @SerializedName(value = "SHAREHOLDSUM")
    private Long shareholdSum;

    @Column(nullable = true)
    @SerializedName(value = "SHARESRATE")
    private Double sharesRate;

    /**
     * 当日收盘价
     */
    @Column(nullable = false,columnDefinition="varchar(50) COMMENT '当日收盘价'")
    @SerializedName(value = "CLOSEPRICE")
    private Double closePrice;

    /**
     * 当日涨跌幅
     */
    @Column(nullable = false,columnDefinition="varchar(20) COMMENT '当日涨跌幅'")
    @SerializedName(value = "ZDF")
    private Double zdf;

    /**
     * 持股市值
     */
    @Column(nullable = false,columnDefinition="varchar(50) COMMENT '持股市值'")
    @SerializedName(value = "SHAREHOLDPRICE")
    private Double shareholdPrice;

    @Column(nullable = true)
    @SerializedName(value = "SHAREHOLDPRICEONE")
    private Double shareholdPriceOne;

    @Column(nullable = true)
    @SerializedName(value = "SHAREHOLDPRICEFIVE")
    private Double shareholdPriceFive;

    @Column(nullable = true)
    @SerializedName(value = "SHAREHOLDPRICETEN")
    private Double shareholdPriceTen;

    @Column(nullable = true)
    @SerializedName(value = "MARKET")
    private String market;

    @Column(nullable = true)
    @SerializedName(value = "ShareHoldSumChg")
    private String ShareHoldSumChg;

    /**
     * 持股数量占A股百分比
     */
    @Column(nullable = false,columnDefinition="varchar(20) COMMENT '持股数量占A股百分比'")
    @SerializedName(value = "Zb")
    private Double zb;

    @Column(nullable = true)
    @SerializedName(value = "Zzb")
    private Double zzb;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;
}
