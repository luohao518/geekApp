package xyz.geekweb.stock.impl;

import xyz.geekweb.stock.FinanceData;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 国债逆回购
 */
public class GZNHGImpl implements FinanceData {

    private final static double MIN_REVERSE_BONDS_VALUE = 4.0;

    private List<RealTimeDataPOJO> data;

    public GZNHGImpl(List<RealTimeDataPOJO> data) {
        this.data = initData(data);
    }

    private List<RealTimeDataPOJO> initData(List<RealTimeDataPOJO> data) {

        return data.stream().filter(item -> item.getFullCode().startsWith("sh204") && item.getNow() >= MIN_REVERSE_BONDS_VALUE).collect(toList());

    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("--------------国债逆回购-------------------\n");
        this.data.forEach(item -> sb.append(String.format("%5s 当前价[%2.2f] 买入价[%2.2f]%n", item.getName(), item.getNow(), item.getBuy1Pricae())));
        sb.append("-------------------------------------------\n");
        return sb.toString();
    }
}
