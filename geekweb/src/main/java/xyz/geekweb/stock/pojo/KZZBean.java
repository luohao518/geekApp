package xyz.geekweb.stock.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author jack.luo
 * @date 2021/3/17
 */
@Data
public class KZZBean {

    //输入条件
    private String input;
    //数量（张）
    private int buyNum;
    //价格
    private double buyPrice;
    //总金额
    private double buyAmount;
    //买几
    private String buyType;
    //折价率
    private double diffPercent;
    //时间
    private Date now;
}
