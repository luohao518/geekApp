package xyz.geekweb.stock.pojo;

import lombok.Data;
import xyz.geekweb.stock.enums.BuyOrSaleEnum;
import xyz.geekweb.stock.enums.FinanceTypeEnum;

/**
 * @author lhao
 */
@Data
public class DataPO {
    /**买入:0 卖出:1*/
    BuyOrSaleEnum buyOrSaleEnum;
    /**类型*/
    FinanceTypeEnum type;
    /**代码*/
    String fullCode;
    /**名称*/
    String name;
    /**今日开盘价*/
    double open;
    /**昨日收盘价*/
    double close;
    /**当前价格*/
    double now;
    /**最高价*/
    double high;
    /**最低价*/
    double low;
    /**竞买价*/
    double buyPrice;
    /**竞卖价*/
    double sellPrice;
    /**成交量*/
    double volume;
    /**基金的净值*/
    double value;
    /**分级基金的净价*/
    double trueValue;

}
