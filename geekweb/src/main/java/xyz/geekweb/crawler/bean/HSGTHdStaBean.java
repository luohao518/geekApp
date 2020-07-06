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

    @Column(nullable = true)
    @SerializedName(value = "HDDATE")
    private String hdDate;

    @Column(nullable = true)
    @SerializedName(value = "HKCODE")
    private String hkCode;

    @Column(nullable = false)
    @SerializedName(value = "SCODE")
    private String sCode;

    @Column(nullable = false)
    @SerializedName(value = "SNAME")
    private String sName;

    @Column(nullable = true)
    @SerializedName(value = "SHAREHOLDSUM")
    private long shareholdSum;

    @Column(nullable = true)
    @SerializedName(value = "SHARESRATE")
    private double sharesRate;

    @Column(nullable = true)
    @SerializedName(value = "CLOSEPRICE")
    private double closePrice;

    @Column(nullable = true)
    @SerializedName(value = "ZDF")
    private double zdf;

    @Column(nullable = true)
    @SerializedName(value = "SHAREHOLDPRICE")
    private double shareholdPrice;

    @Column(nullable = true)
    @SerializedName(value = "SHAREHOLDPRICEONE")
    private double shareholdPriceOne;

    @Column(nullable = true)
    @SerializedName(value = "SHAREHOLDPRICEFIVE")
    private double shareholdPriceFive;

    @Column(nullable = true)
    @SerializedName(value = "SHAREHOLDPRICETEN")
    private double shareholdPriceTen;

    @Column(nullable = true)
    @SerializedName(value = "MARKET")
    private String market;

    @Column(nullable = true)
    @SerializedName(value = "ShareHoldSumChg")
    private String ShareHoldSumChg;

    @Column(nullable = true)
    @SerializedName(value = "Zb")
    private double zb;

    @Column(nullable = true)
    @SerializedName(value = "Zzb")
    private double zzb;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;
}
