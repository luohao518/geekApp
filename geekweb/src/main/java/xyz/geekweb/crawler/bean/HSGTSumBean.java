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
        private static final Long serialVersionUID = 1L;

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
        private Integer jgSum;

        @Column(nullable = true)
        @SerializedName(value = "SharesRate")
        private Double sharesRate;

        @Column(nullable = true)
        @SerializedName(value = "NewPrice")
        private Double newPrice;

        @Column(nullable = true)
        @SerializedName(value = "Zdf")
        private Double zdf;

        @Column(nullable = true)
        @SerializedName(value = "Market")
        private String market;

        @Column(nullable = true)
        @SerializedName(value = "ShareHold")
        private Long shareHold;

        @Column(nullable = true)
        @SerializedName(value = "ShareSZ")
        private Double shareSz;

        @Column(nullable = true)
        @SerializedName(value = "LTZB")
        private Double ltzb;

        @Column(nullable = true)
        @SerializedName(value = "ZZB")
        private Double zzb;

        @Column(nullable = true)
        @SerializedName(value = "LTSZ")
        private Double ltsz;

        @Column(nullable = true)
        @SerializedName(value = "ZSZ")
        private Double zsz;

        @Column(nullable = true)
        @SerializedName(value = "ShareHold_Before_One")
        private Integer shareHoldBeforeOne;

        @Column(nullable = true)
        @SerializedName(value = "ShareSZ_Before_One")
        private Integer shareszBeforeOne;

        @Column(nullable = true)
        @SerializedName(value = "ShareHold_Chg_One")
        private String shareHoldChgOne;

        @Column(nullable = true)
        @SerializedName(value = "ShareSZ_Chg_One")
        private Double shareszChgOne;

        @Column(nullable = true)
        @SerializedName(value = "ShareSZ_Chg_Rate_One")
        private Double shareszChgRateOne;

        @Column(nullable = true)
        @SerializedName(value = "LTZB_One")
        private Double ltzbOne;

        @Column(nullable = true)
        @SerializedName(value = "ZZB_One")
        private Double zzbOne;

        @Column(nullable = false)
        private Date createDate;

        @Column(nullable = false)
        private Date updateDate;
    }
