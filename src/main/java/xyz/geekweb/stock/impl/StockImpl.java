package xyz.geekweb.stock.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.geekweb.stock.DataProperties;
import xyz.geekweb.stock.FinanceData;
import xyz.geekweb.stock.mq.Sender;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 股票
 */
@Service
public class StockImpl implements FinanceData {

    private List<RealTimeDataPOJO> data;
    private List<RealTimeDataPOJO> watchData;

    private DataProperties dataProperties;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public StockImpl(DataProperties dataProperties) {

        this.dataProperties = dataProperties;
    }


    public void fetchData(List<RealTimeDataPOJO> data) {

        this.data = data.stream().filter(item -> (
                item.getType()==RealTimeDataPOJO.INDEX ||
                item.getFullCode().startsWith("sh60") ||
                        item.getFullCode().startsWith("sz00")) ).collect(toList());
        this.watchData = data.stream().filter(item -> (item.getFullCode().startsWith("sh60") || item.getFullCode().startsWith("sz00")) && Math.abs(((item.getNow() - item.getClose()) / item.getClose()) * 100) >= Double.parseDouble(dataProperties.getMap().get("MAX_STOCKS_PERCENT"))).collect(toList());
    }

    @Override
    public void sendNotify(Sender sender){
        //sender.sendNotify(this.watchData);
    }

    @Override
    public void printInfo() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("-------------------股票-------------------\n");
                this.data.forEach(item -> {
                    if(item.getType()==RealTimeDataPOJO.INDEX){
                        sb.append(String.format("%4s 当前价[%8.2f] 涨跌额[%6.2f] 涨跌百分比[%6.2f%%] 成交金额[%,10.0f万]%n",
                                item.getName(), item.getNow(), item.getRiseAndFall(),
                                item.getRiseAndFallPercent(),item.getVolumePrice()));
                    }else {
                        sb.append(String.format("%8s 当前价[%6.2f] 卖出价[%6.2f] 买入价[%6.2f] 涨跌幅[%6.2f] %-6s %n", item.getFullCode(), item.getNow(), item.getSell1Pricae(), item.getBuy1Pricae(), (((item.getNow() - item.getClose()) / item.getClose()) * 100), item.getName()));
                    }
                });

        sb.append("-------------------------------------------\n");
        logger.info(sb.toString());
    }
}
