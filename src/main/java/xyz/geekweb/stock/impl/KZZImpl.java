package xyz.geekweb.stock.impl;

import xyz.geekweb.stock.FinanceData;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 可转债,元和
 */
public class KZZImpl implements FinanceData {

    private final static double MIN_132003_VALUE = 99.500;

    private final static double MIN_505888_VALUE = 1.013;

    private List<RealTimeDataPOJO> data;

    public KZZImpl(List<RealTimeDataPOJO> realTimeDataPOJO) {
        this.data = initData(realTimeDataPOJO);
    }

    private List<RealTimeDataPOJO> initData(List<RealTimeDataPOJO> realTimeDataPOJO) {


        return realTimeDataPOJO.stream().filter(item ->
                ((item.getFullCode().startsWith("sh505888") && item.getNow() <= MIN_505888_VALUE) || (item.getFullCode().startsWith("sh132003") && item.getNow() <= MIN_132003_VALUE))).collect(toList());

    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("--------------可转债,元和-------------------\n");
        this.data.forEach(item -> sb.append(String.format("%-6s 当前价[%7.3f] 卖出价[%7.3f] 卖量[%5.0f] %-6s %n", item.getFullCode(), item.getNow(), item.getSell1Pricae(), item.getSell1Num(), item.getName())));
        sb.append("-------------------------------------------\n");
        return sb.toString();
    }
}
