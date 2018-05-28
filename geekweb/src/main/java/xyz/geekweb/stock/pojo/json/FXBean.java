package xyz.geekweb.stock.pojo.json;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lhao
 * @date 2018/4/27
 */
@Data
public class FXBean implements Serializable{

    private String symbol;
    private double bid;
    private double ask;
    private double price;
    private long timestamp;
    private Date time;


}