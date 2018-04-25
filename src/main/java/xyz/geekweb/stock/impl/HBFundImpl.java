package xyz.geekweb.stock.impl;

import xyz.geekweb.stock.FinanceData;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 货币基金
 */
public class HBFundImpl implements FinanceData<RealTimeDataPOJO> {

    private final static double MAX_MONETARY_FUNDS_VALUE = 99.990;

    private List<RealTimeDataPOJO> data;

    @Override
    public void initData(List<RealTimeDataPOJO> data) {

        this.data = data.stream().filter(item -> item.getFullCode().startsWith("sh511") && item.getNow() <= MAX_MONETARY_FUNDS_VALUE).collect(toList());

    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("--------------货币基金--------------------\n");
        this.data.forEach( item -> sb.append(String.format("购买货币基金:%s 当前价[%7.3f] 卖出价[%7.3f] 卖量[%5.0f]%n", item.getFullCode(), item.getNow(), item.getSell1Pricae(), item.getSell1Num())));
        sb.append("-------------------------------------------\n");
        return sb.toString();
    }
}
