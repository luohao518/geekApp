package xyz.geekweb.stock.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.geekweb.stock.DataProperties;
import xyz.geekweb.stock.FinanceData;
import xyz.geekweb.stock.mq.Sender;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.*;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 可转债,元和
 */
@Service
@Deprecated
public class KZZImpl implements FinanceData {

    private List<RealTimeDataPOJO> data;
    private List<RealTimeDataPOJO> watchData;

    private DataProperties dataProperties;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public KZZImpl(DataProperties dataProperties) {
        this.dataProperties = dataProperties;
    }

    public void fetchData(List<RealTimeDataPOJO> realTimeDataPOJO) {

        final double low_132003_value = Double.parseDouble(this.dataProperties.getMap().get("132003_VALUE").split(",")[0]);
        final double up_132003_value = Double.parseDouble(this.dataProperties.getMap().get("132003_VALUE").split(",")[1]);

        final double low_505888_value = Double.parseDouble(this.dataProperties.getMap().get("505888_VALUE").split(",")[0]);
        final double up_505888_value = Double.parseDouble(this.dataProperties.getMap().get("505888_VALUE").split(",")[1]);

        this.data = realTimeDataPOJO.stream().filter(item ->( item.getFullCode().startsWith("sh505888")  || item.getFullCode().startsWith("sh132003"))).collect(toList());

        this.watchData = realTimeDataPOJO.stream().filter(item ->
                ((item.getFullCode().startsWith("sh505888") && item.getNow() <= low_505888_value) || (item.getFullCode().startsWith("sh132003") && item.getNow() <= low_132003_value))).collect(toList());

    }

    @Override
    public void sendNotify(Sender sender){
       // sender.sendNotify(this.watchData);
    }


    @Override
    public void printInfo() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("--------------可转债,元和--------------\n");
        this.data.forEach(item -> sb.append(String.format("%-6s 当前价[%7.3f] 卖出价[%7.3f] 卖量[%5.0f] %-6s %n", item.getFullCode(), item.getNow(), item.getSell1Pricae(), item.getSell1Num(), item.getName())));
        sb.append("--------------------------------------\n");
        logger.info(sb.toString());
    }


}
