package xyz.geekweb.stock.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import xyz.geekweb.config.DataProperties;
import xyz.geekweb.stock.mq.Sender;
import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeDataPOJO;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 可转债,元和
 */
@Service
@Slf4j
public class KZZImpl implements FinanceData {

    private List<RealTimeDataPOJO> data;
    private List<RealTimeDataPOJO> watchData;

    private DataProperties dataProperties;

    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    @Autowired
    public KZZImpl(DataProperties dataProperties) {
        this.dataProperties = dataProperties;
    }

    public void fetchData(List<RealTimeDataPOJO> realTimeDataPOJO) {

        final double low_132003_value = Double.parseDouble(this.dataProperties.getMap().get("132003_VALUE").split(",")[0]);
        final double up_132003_value = Double.parseDouble(this.dataProperties.getMap().get("132003_VALUE").split(",")[1]);

        final double low_505888_value = Double.parseDouble(this.dataProperties.getMap().get("505888_VALUE").split(",")[0]);
        final double up_505888_value = Double.parseDouble(this.dataProperties.getMap().get("505888_VALUE").split(",")[1]);

        this.data = realTimeDataPOJO.stream().filter(item -> (item.getFullCode().startsWith("sh505888") || item.getFullCode().startsWith("sh132003"))).collect(toList());

        this.watchData = realTimeDataPOJO.stream().filter(item ->
                ((item.getFullCode().startsWith("sh505888") && item.getNow() <= low_505888_value) || (item.getFullCode().startsWith("sh132003") && item.getNow() <= low_132003_value))).collect(toList());

    }


    public void fetchKZZData(List<RealTimeDataPOJO> realTimeDataPOJO) {

        List<String> codeList=new ArrayList<>();
        String[] kzzes = this.dataProperties.getMap().get("kzz").split(";");
        for (String kzz: kzzes) {
            log.debug("parse:{}",kzz);
            String[] codes = kzz.split(":");
            Assert.isTrue(codes.length==3,"must be 可转债代码:股票代码：转股价");
            List<RealTimeDataPOJO> searchResult = realTimeDataPOJO.stream().filter(item -> (item.getFullCode().startsWith(codes[0]) || item.getFullCode().startsWith(codes[1]))).collect(toList());
            Assert.isTrue(searchResult.size()==2,"must be two items");
            DecimalFormat dfNum = new DecimalFormat("#0");
            float basePrice = Float.parseFloat(codes[2]);
            String fullCode = searchResult.get(0).getFullCode();
            double stockBuy1Price = searchResult.get(1).getBuy1Price();
            int stockBuy1Num = (int)(searchResult.get(1).getBuy1Num()/100);

            boolean isSH = fullCode.startsWith("sh");
            boolean isSZ = fullCode.startsWith("sz");

            //取最大可买到值
            int kzzSellNum =0;
            double kzzSellPrice = 0.0d;
            int min = 10;
            if(isSZ){
                min = 100;
            }
            if(searchResult.get(0).getSell1Num()> min){
                log.debug("try sell1：{}",searchResult.get(0).getSell1Num());
                kzzSellNum =(int)(searchResult.get(0).getSell1Num());
                kzzSellPrice = searchResult.get(0).getSell1Price();
            }else if(searchResult.get(0).getSell2Num()>min){
                log.debug("try sell2：{}",searchResult.get(0).getSell2Num());
                kzzSellNum =(int)(searchResult.get(0).getSell2Num());
                kzzSellPrice = searchResult.get(0).getSell2Price();
            }else if(searchResult.get(0).getSell3Num()>min){
                log.debug("try sell3：{}",searchResult.get(0).getSell3Num());
                kzzSellNum =(int)(searchResult.get(0).getSell3Num());
                kzzSellPrice = searchResult.get(0).getSell3Price();
            }else if(searchResult.get(0).getSell4Num()>min){
                log.debug("try sell4：{}",searchResult.get(0).getSell4Num());
                kzzSellNum =(int)(searchResult.get(0).getSell4Num());
                kzzSellPrice = searchResult.get(0).getSell4Price();
            }else if(searchResult.get(0).getSell5Num()>min){
                log.debug("try sell5：{}",searchResult.get(0).getSell5Num());
                kzzSellNum =(int)(searchResult.get(0).getSell5Num());
                kzzSellPrice = searchResult.get(0).getSell5Price();
            }


            double diffPercent=(((kzzSellPrice/100*basePrice)-stockBuy1Price)/stockBuy1Price)*100;

            log.debug("[{}:{}] {}% buy[{}:{}] sell[{}:{}]", codes[0],codes[1], decimalFormat.format(diffPercent),kzzSellNum, kzzSellPrice,stockBuy1Num,stockBuy1Price);
            if(diffPercent<-1.0d && kzzSellNum > min && stockBuy1Num>100){
                log.info("[{}:{}] {}% buy[{}:{}] sell[{}:{}]", codes[0],codes[1], decimalFormat.format(diffPercent),kzzSellNum, kzzSellPrice,stockBuy1Num,stockBuy1Price);
            }
        }

    }

    @Override
    public void printInfo() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("--------------可转债--------------\n");
        this.data.forEach(item -> sb.append(String.format("%-6s 当前价[%7.3f] 卖出价[%7.3f] 卖量[%5.0f] %-6s %n", item.getFullCode(), item.getNow(), item.getSell1Price(), item.getSell1Num(), item.getName())));
        sb.append("--------------------------------------\n");
        log.info(sb.toString());
    }

    @Override
    public void sendNotify(Sender sender) {
        // sender.sendNotify(this.watchData);
    }

    @Override
    public List<RealTimeDataPOJO> getData() {
        return this.data;
    }


}
