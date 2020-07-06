/**
  * Copyright 2020 bejson.com 
  */
package xyz.geekweb.crawler.bean.kzz;
import lombok.Data;


/**
 * Auto-generated: 2020-07-06 16:56:51
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Cell {

    private String stock_nm;
    private Double redeem_orders;
    private String redeem_price;
    private String btype;
    private String redeem_dt;
    private String redeem_count;
    private String redeem_flag;
    private String margin_flg;
    private Integer after_next_put_dt;
    private String convert_dt;
    /** 转债价格*/
    private String price;
    private String orig_iss_amt;
    /** 转股价*/
    private String convert_price;
    private String redeem_icon;
    private String force_redeem;
    private String next_put_dt;
    private String bond_id;
    private String real_force_redeem_price;
    private Integer redeem_real_days;
    private String curr_iss_amt;
    private String redeem_tc;
    private Integer redeem_count_days;
    /** 股票价格*/
    private String sprice;
    private String force_redeem_price;
    /** 股票代码*/
    private String stock_id;
    private String bond_nm;
    private Integer redeem_total_days;
    private String redeem_price_ratio;

    public double calculatPrice(){
        //股票价格
        double dSprice = Double.parseDouble(this.sprice);
        //转债价格
        double dPrice = Double.parseDouble(this.price);
        //转股价
        double dConvertPrice = Double.parseDouble(this.convert_price);

        //溢价率计算
        double price1=((dSprice-dConvertPrice)/dConvertPrice)*100+100;
        return ((dPrice-price1)/price1)*100;
    }

}