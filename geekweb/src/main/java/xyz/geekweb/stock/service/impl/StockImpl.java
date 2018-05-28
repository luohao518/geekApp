package xyz.geekweb.stock.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.geekweb.config.DataProperties;
import xyz.geekweb.stock.mq.Sender;
import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeDataPOJO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 股票
 */
@Service
public class StockImpl implements FinanceData{

    private List<RealTimeDataPOJO> data;
    private  List<RealTimeDataPOJO> watchData;

    private  DataProperties dataProperties;
    private  Logger logger = LoggerFactory.getLogger(this.getClass());

    public StockImpl() {
    }

    @Autowired
    public StockImpl(DataProperties dataProperties) {

        this.dataProperties = dataProperties;
    }

    @Override
    public List<RealTimeDataPOJO>  getData(){
        return this.data;
    }

    public void fetchData(List<RealTimeDataPOJO> data) {

        this.data = data.stream().filter(item -> (
                item.getType()==RealTimeDataPOJO.INDEX ||
                        (StringUtils.startsWithAny(item.getFullCode(),new String[]{"sh","sz","int"})
                        && !StringUtils.startsWithAny(item.getFullCode(),new String[]{"sh511","sh204"})))).collect(toList());
        this.watchData = this.data.stream().filter( item -> item.getType()== RealTimeDataPOJO.STOCK).filter(item -> Math.abs(item.getRiseAndFallPercent()) >= Double.parseDouble(dataProperties.getMap().get("MAX_STOCKS_PERCENT"))).collect(toList());
    }

    @Override
    public void sendNotify(Sender sender){
        sender.sendNotify(this.watchData);
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
                        sb.append(String.format("%8s 当前价[%6.2f] 卖出价[%6.2f]  卖量[%7.0f] 买入价[%6.2f] 涨跌幅[%6.2f%%] %-6s %n",
                                item.getFullCode(), item.getNow(), item.getSell1Price(), item.getSell1Num(), item.getBuy1Price(), item.getRiseAndFallPercent(), item.getName()));
                    }
                });

        sb.append("-------------------------------------------\n");
        logger.info(sb.toString());
    }

    public static void calcu132003(double currentPrice){

        //4.26才可以开始计算回售条款
        LocalDate startDate = LocalDate.of(2018, 4, 26);
        LocalDate endDate=startDate;
        //30工作日观察（5.1假期)+2工作日公告
        for(int i=0;i<30+1+2;i++) {
            endDate = getNextWorkDate(endDate);
        }

        //计算年华利率
        double endPrice=100d;
        long days=endDate.toEpochDay()- LocalDate.now().toEpochDay();
        double percent=(((endPrice-currentPrice)/currentPrice)/days)*365*100;
        System.out.println(String.format("公告完成日[%s]   剩余天数[%d天]   年华利率[%5.2f%%]", endDate,days,percent));

        //20天的资金回来（考虑一般公募基金会接盘）
        endDate=endDate.plusDays(20);

        days=endDate.toEpochDay()- LocalDate.now().toEpochDay();
        //计算年华利率
        endPrice=100d;
        percent=(((endPrice-currentPrice)/currentPrice)/days)*365*100;
        System.out.println(String.format("最终完成日[%s]   剩余天数[%d天]   年华利率[%5.2f%%]", endDate,days,percent));
    }

    private static LocalDate getNextWorkDate(LocalDate startDate) {
        return startDate.with( temporal -> {
            // 当前日期
            DayOfWeek dayOfWeek = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));

            // 正常情况下，每次增加一天
            int dayToAdd = 1;

            // 如果是星期五，增加三天
            if (dayOfWeek == DayOfWeek.FRIDAY) {
                dayToAdd = 3;
            }

            // 如果是星期六，增加两天
            if (dayOfWeek == DayOfWeek.SATURDAY) {
                dayToAdd = 2;
            }

            return temporal.plus(dayToAdd, ChronoUnit.DAYS);
        });
    }

    public   static void main(String[] args){

        StockImpl.calcu132003(99.48d);
    }
}
