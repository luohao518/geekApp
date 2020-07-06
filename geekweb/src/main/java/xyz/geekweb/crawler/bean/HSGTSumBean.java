package xyz.geekweb.crawler.bean;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author lhao
 * @date 2020/7/5
 */
@Data
@Entity
public class HSGTSumBean implements Serializable {
        private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue
        private Long id;

        /**
         * 自定义字段
         */
        @Column(nullable = false)
        private Long flg = 0L;

        @Column(nullable = true)
        @SerializedName(value = "DateType")
        private String dateType;

        @Column(nullable = false)
        @SerializedName(value = "HdDate")
        private String hdDate;

        @Column(nullable = true)
        @SerializedName(value = "Hkcode")
        private String hkcode;

        @Column(nullable = false)
        @SerializedName(value = "SCode")
        private String sCode;

        @Column(nullable = false)
        @SerializedName(value = "SName")
        private String sName;

        @Column(nullable = true)
        @SerializedName(value = "HYName")
        private String hyName;

        @Column(nullable = true)
        @SerializedName(value = "HYCode")
        private String hyCode;

        @Column(nullable = true)
        @SerializedName(value = "ORIGINALCODE")
        private String oRIGINALCode;

        @Column(nullable = true)
        @SerializedName(value = "DQName")
        private String dqName;

        @Column(nullable = true)
        @SerializedName(value = "DQCode")
        private String dqCode;

        @Column(nullable = true)
        @SerializedName(value = "ORIGINALCODE_DQ")
        private String originalCodeDq;

        @Column(nullable = true)
        @SerializedName(value = "JG_SUM")
        private int jgSum;

        @Column(nullable = true)
        @SerializedName(value = "SharesRate")
        private double sharesRate;

        @Column(nullable = true)
        @SerializedName(value = "NewPrice")
        private double newPrice;

        @Column(nullable = true)
        @SerializedName(value = "Zdf")
        private double zdf;

        @Column(nullable = true)
        @SerializedName(value = "Market")
        private String market;

        @Column(nullable = true)
        @SerializedName(value = "ShareHold")
        private long shareHold;

        @Column(nullable = true)
        @SerializedName(value = "ShareSZ")
        private double shareSz;

        @Column(nullable = true)
        @SerializedName(value = "LTZB")
        private double ltzb;

        @Column(nullable = true)
        @SerializedName(value = "ZZB")
        private double zzb;

        @Column(nullable = true)
        @SerializedName(value = "LTSZ")
        private double ltsz;

        @Column(nullable = true)
        @SerializedName(value = "ZSZ")
        private double zsz;

        @Column(nullable = true)
        @SerializedName(value = "ShareHold_Before_One")
        private int shareHoldBeforeOne;

        @Column(nullable = true)
        @SerializedName(value = "ShareSZ_Before_One")
        private int shareszBeforeOne;

        @Column(nullable = true)
        @SerializedName(value = "ShareHold_Chg_One")
        private long shareHoldChgOne;

        @Column(nullable = true)
        @SerializedName(value = "ShareSZ_Chg_One")
        private double shareszChgOne;

        @Column(nullable = true)
        @SerializedName(value = "ShareSZ_Chg_Rate_One")
        private double shareszChgRateOne;

        @Column(nullable = true)
        @SerializedName(value = "LTZB_One")
        private double ltzbOne;

        @Column(nullable = true)
        @SerializedName(value = "ZZB_One")
        private double zzbOne;

        @Column(nullable = false)
        private Date createDate;

        @Column(nullable = false)
        private Date updateDate;
    }
