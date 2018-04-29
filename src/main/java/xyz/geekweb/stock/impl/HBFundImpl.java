package xyz.geekweb.stock.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.geekweb.stock.DataProperties;
import xyz.geekweb.stock.FinanceData;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 货币基金
 */
@Service
public class HBFundImpl implements FinanceData {


    private List<RealTimeDataPOJO> data;


    private DataProperties dataProperties;

    @Autowired
    public HBFundImpl(DataProperties dataProperties) {
        this.dataProperties = dataProperties;

    }

    public void initData(List<RealTimeDataPOJO> data) {
        final double low_monetary_funds_value = Double.parseDouble(this.dataProperties.getMap().get("MONETARY_FUNDS_VALUE").split(",")[0]);
        final double up_monetary_funds_value = Double.parseDouble(this.dataProperties.getMap().get("MONETARY_FUNDS_VALUE").split(",")[1]);

        this.data = data.stream().filter(item -> item.getFullCode().startsWith("sh511") && item.getNow() <= low_monetary_funds_value).collect(toList());

    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("------------货币基金---------------\n");
        this.data.forEach(item -> sb.append(String.format("购买货币基金:%s 当前价[%7.3f] 卖出价[%7.3f] 卖量[%5.0f]%n", item.getFullCode(), item.getNow(), item.getSell1Pricae(), item.getSell1Num())));
        sb.append("-----------------------------------\n");
        return sb.toString();
    }
}
