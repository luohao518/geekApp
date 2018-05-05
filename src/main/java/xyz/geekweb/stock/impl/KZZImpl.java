package xyz.geekweb.stock.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.geekweb.stock.DataProperties;
import xyz.geekweb.stock.FinanceData;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.*;
import java.util.List;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 可转债,元和
 */
@Service
public class KZZImpl implements FinanceData {

    private List<RealTimeDataPOJO> data;

    private DataProperties dataProperties;


    @Autowired
    public KZZImpl(DataProperties dataProperties) {
        this.dataProperties = dataProperties;
    }

    public void initData(List<RealTimeDataPOJO> realTimeDataPOJO) {

        final double low_132003_value = Double.parseDouble(this.dataProperties.getMap().get("132003_VALUE").split(",")[0]);
        final double up_132003_value = Double.parseDouble(this.dataProperties.getMap().get("132003_VALUE").split(",")[1]);

        final double low_505888_value = Double.parseDouble(this.dataProperties.getMap().get("505888_VALUE").split(",")[0]);
        final double up_505888_value = Double.parseDouble(this.dataProperties.getMap().get("505888_VALUE").split(",")[1]);

        this.data = realTimeDataPOJO.stream().filter(item ->
                ((item.getFullCode().startsWith("sh505888") && item.getNow() <= low_505888_value) || (item.getFullCode().startsWith("sh132003") && item.getNow() <= low_132003_value))).collect(toList());

    }

    @Override
    public boolean isNotify(){
        return this.data!=null && this.data.size()>0;
    }

    @Override
    public String toPrintout() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("--------------可转债,元和--------------\n");
        this.data.forEach(item -> sb.append(String.format("%-6s 当前价[%7.3f] 卖出价[%7.3f] 卖量[%5.0f] %-6s %n", item.getFullCode(), item.getNow(), item.getSell1Pricae(), item.getSell1Num(), item.getName())));
        sb.append("--------------------------------------\n");
        return sb.toString();
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
        double endPrice=100.7d;
        long days=endDate.toEpochDay()- LocalDate.now().toEpochDay();
        double percent=(((endPrice-currentPrice)/currentPrice)/days)*365*100;
        System.out.println(String.format("公告完成日[%s]   剩余天数[%d天]   年华利率[%5.2f%%]", endDate,days,percent));

        //20天的资金回来（考虑一般公募基金会接盘）
        endDate=endDate.plusDays(20);

        days=endDate.toEpochDay()- LocalDate.now().toEpochDay();
        //计算年华利率
        endPrice=100.8d;
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

        KZZImpl.calcu132003(99.70d);
    }
}
