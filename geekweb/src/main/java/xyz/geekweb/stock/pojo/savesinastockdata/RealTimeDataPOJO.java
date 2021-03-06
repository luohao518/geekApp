package xyz.geekweb.stock.pojo.savesinastockdata;

import lombok.Data;
import xyz.geekweb.stock.enums.BuyOrSaleEnum;
import xyz.geekweb.stock.enums.FinanceTypeEnum;

import java.io.Serializable;
import java.util.Date;

/**
 * 负责储存实时数据的对象
 *
 * @author yilihjy Email:yilihjy@gmail.com
 * @version 1.0.0
 */
@Data
public class RealTimeDataPOJO implements Serializable {
    /**
     * 数据类型为指数
     */
    public static final int INDEX = 1;

    /**
     * 数据类型为股票
     */
    public static final int STOCK = 2;

    /**
     * 买入:0 卖出:1
     */
    BuyOrSaleEnum buyOrSaleEnum;
    /**
     * 查询类型
     */
    FinanceTypeEnum searchType;

    /**
     * 基金的净值
     */
    double value;
    /**
     * 分级基金的净价
     */
    double trueValue;
    private int type;
    private String fullCode;
    private String name;
    private double open;
    private double close;
    private double now;
    private double high;
    private double low;
    private double buyPrice;
    private double sellPrice;
    private double volume;
    private double volumePrice;
    private double buy1Num;
    private double buy1Price;
    private double buy2Num;
    private double buy2Price;
    private double buy3Num;
    private double buy3Price;
    private double buy4Num;
    private double buy4Price;
    private double buy5Num;
    private double buy5Price;
    private double sell1Num;
    private double sell1Price;
    private double sell2Num;
    private double sell2Price;
    private double sell3Num;
    private double sell3Price;
    private double sell4Num;
    private double sell4Price;
    private double sell5Num;
    private double sell5Price;
    private Date date;
    private Date time;
    private double riseAndFall;
    private double riseAndFallPercent;

    /**
     * 无参数构造方法
     */
    public RealTimeDataPOJO() {

    }

    /**
     * 指数的构造方法
     *
     * @param type               数据类型   INDEX
     * @param fullCode           指数代码 如s_sh000001
     * @param name               指数名称
     * @param now                当前价
     * @param volume             成交量
     * @param volumePrice        成就金额
     * @param riseAndFall        涨跌额
     * @param riseAndFallPercent 涨跌百分比
     */
    public RealTimeDataPOJO(int type, String fullCode, String name, double now, double volume, double volumePrice,
                            double riseAndFall, double riseAndFallPercent) {
        super();
        this.type = type;
        this.fullCode = fullCode;
        this.name = name;
        this.now = now;
        this.volume = volume;
        this.volumePrice = volumePrice;
        this.riseAndFall = riseAndFall;
        this.riseAndFallPercent = riseAndFallPercent;
    }

    /**
     * 股票的构造方法
     *
     * @param type               应为STOCK
     * @param fullCode           股票代码 如sz000001
     * @param name               股票名称
     * @param open               今日开盘价
     * @param close              昨日收盘价
     * @param now                当前价
     * @param high               最高价
     * @param low                最低价
     * @param buyPrice           竞买价
     * @param sellPrice          竞卖价
     * @param volume             成交量
     * @param volumePrice        成交总金额
     * @param buy1Num            买一申请数
     * @param buy1Price          买一报价
     * @param buy2Num            买二申请数
     * @param buy2Price          买二报价
     * @param buy3Num            买三申请数
     * @param buy3Price          买三报价
     * @param buy4Num            买四申请数
     * @param buy4Price          买四报价
     * @param buy5Num            买五申请数
     * @param buy5Price          买五报价
     * @param sell1Num           卖一申请数
     * @param sell1Price         卖一报价
     * @param sell2Num           卖二申请数
     * @param sell2Price         卖二报价
     * @param sell3Num           卖三申请数
     * @param sell3Price         卖三报价
     * @param sell4Num           卖四申请数
     * @param sell4Price         卖四报价
     * @param sell5Num           卖五申请数
     * @param sell5Price         卖五报价
     * @param date               日期
     * @param time               时间
     * @param riseAndFallPercent 涨跌百分比
     */
    public RealTimeDataPOJO(int type, String fullCode, String name, double open, double close, double now, double high,
                            double low, double buyPrice, double sellPrice, double volume, double volumePrice, double buy1Num,
                            double buy1Price, double buy2Num, double buy2Price, double buy3Num, double buy3Price, double buy4Num,
                            double buy4Price, double buy5Num, double buy5Price, double sell1Num, double sell1Price, double sell2Num,
                            double sell2Price, double sell3Num, double sell3Price, double sell4Num, double sell4Price,
                            double sell5Num, double sell5Price, Date date, Date time, double riseAndFallPercent) {
        super();
        this.type = type;
        this.fullCode = fullCode;
        this.name = name;
        this.open = open;
        this.close = close;
        this.now = now;
        this.high = high;
        this.low = low;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.volume = volume;
        this.volumePrice = volumePrice;
        this.buy1Num = buy1Num;
        this.buy1Price = buy1Price;
        this.buy2Num = buy2Num;
        this.buy2Price = buy2Price;
        this.buy3Num = buy3Num;
        this.buy3Price = buy3Price;
        this.buy4Num = buy4Num;
        this.buy4Price = buy4Price;
        this.buy5Num = buy5Num;
        this.buy5Price = buy5Price;
        this.sell1Num = sell1Num;
        this.sell1Price = sell1Price;
        this.sell2Num = sell2Num;
        this.sell2Price = sell2Price;
        this.sell3Num = sell3Num;
        this.sell3Price = sell3Price;
        this.sell4Num = sell4Num;
        this.sell4Price = sell4Price;
        this.sell5Num = sell5Num;
        this.sell5Price = sell5Price;
        this.date = date;
        this.time = time;
        this.value = 0.0d;
        this.trueValue = 0.0d;
    }
}
