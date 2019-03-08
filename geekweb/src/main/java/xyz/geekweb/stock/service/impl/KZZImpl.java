package xyz.geekweb.stock.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import xyz.geekweb.config.DataProperties;
import xyz.geekweb.stock.mq.Sender;
import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeDataPOJO;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 可转债,元和
 */
@Service
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

        this.data = realTimeDataPOJO.stream().filter(item -> (item.getFullCode().startsWith("sh505888") || item.getFullCode().startsWith("sh132003"))).collect(toList());

        this.watchData = realTimeDataPOJO.stream().filter(item ->
                ((item.getFullCode().startsWith("sh505888") && item.getNow() <= low_505888_value) || (item.getFullCode().startsWith("sh132003") && item.getNow() <= low_132003_value))).collect(toList());

    }


    public void fetchKZZData(List<RealTimeDataPOJO> realTimeDataPOJO) {

        List<String> codeList=new ArrayList<>();
        String[] kzzes = this.dataProperties.getMap().get("kzz").split(";");
        for (String kzz: kzzes) {
            String[] codes = kzz.split(":");
            Assert.isTrue(codes.length==3,"must be 可转债代码:股票代码：转股价");
            List<RealTimeDataPOJO> searchResult = realTimeDataPOJO.stream().filter(item -> (item.getFullCode().startsWith(codes[0]) || item.getFullCode().startsWith(codes[1]))).collect(toList());
            Assert.isTrue(searchResult.size()==2,"must be two items");
            float basePrice = Float.parseFloat(codes[2]);
            String fullCode = searchResult.get(0).getFullCode();
            double kzzSell1Price = searchResult.get(0).getSell1Price();
            double kzzSell1Num = searchResult.get(0).getSell1Num();
            double stockBuy1Price = searchResult.get(1).getBuy1Price();
            double stockBuy1Num = searchResult.get(1).getBuy1Num();
            double diffPercent=((kzzSell1Price/100*basePrice)-stockBuy1Price)/stockBuy1Price;
            System.out.println("aaaaaaaaaaaaa="+fullCode+"bbbb"+diffPercent);
            if(diffPercent<-0.010){
                if(fullCode.startsWith("sh")){
                    if(kzzSell1Num>10 && stockBuy1Num>100){

                        StringBuilder sb = new StringBuilder();
                        sb.append("--------------watch--------------\n");
                        sb.append(String.format("%-6s diff[%7.3f] kzz[5.0f] stock[5.0f] %-6s %n", fullCode, kzzSell1Num, stockBuy1Num, diffPercent*100));
                        sb.append("--------------------------------------\n");
                        System.out.println(sb.toString());

                    }
                }else{
                    System.out.println(kzzSell1Num);
                    System.out.println(stockBuy1Num);
                    if(kzzSell1Num>100 && stockBuy1Num>100){

                        StringBuilder sb = new StringBuilder();
                        sb.append("--------------watch--------------\n");
                        sb.append(String.format("%-6s diff[%7.3f] kzz[5.0f] stock[5.0f] %-6s %n", fullCode, kzzSell1Num, stockBuy1Num, diffPercent*100));
                        sb.append("--------------------------------------\n");
                        System.out.println(sb.toString());

                    }
                }
            }else if(diffPercent<-0.015){

            }else if(diffPercent<-0.020){

            }



        }

    }

    @Override
    public void printInfo() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("--------------可转债--------------\n");
        this.data.forEach(item -> sb.append(String.format("%-6s 当前价[%7.3f] 卖出价[%7.3f] 卖量[%5.0f] %-6s %n", item.getFullCode(), item.getNow(), item.getSell1Price(), item.getSell1Num(), item.getName())));
        sb.append("--------------------------------------\n");
        logger.info(sb.toString());
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
