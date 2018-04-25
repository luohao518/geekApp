package xyz.geekweb.stock.impl;

import xyz.geekweb.stock.FinanceData;
import xyz.geekweb.stock.pojo.FJFundaPO;
import xyz.geekweb.stock.pojo.json.JsonRootBean;
import xyz.geekweb.stock.pojo.json.Rows;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 分级基金
 */
public class FjFundImpl implements FinanceData<Rows> {


    private final static String[] FJ_FUNDS = {"150022", "150181", "150018", "150171", "150227", "150200",};
    private final static String[] FJ_FUNDS_HAVE = {"150181", "150018"};
    private final static double MAX_DIFF_VALUE = 0.003;

    private List<FJFundaPO> data;

    @Override
    public void initData(List<Rows> rows) {
        List<String> strFjFunds = Arrays.asList(FJ_FUNDS);
        List<FJFundaPO> lstFJFundaPO = new ArrayList<>(10);

        rows.forEach(row -> {
            if (strFjFunds.contains(row.getId())) {
                double fundaCurrentPrice = Double.parseDouble(row.getCell().getFunda_current_price());
                double fundaValue = Double.parseDouble(row.getCell().getFunda_value());
                double diffValue = fundaCurrentPrice - (fundaValue - 1.0);
                FJFundaPO item = new FJFundaPO();
                item.setFundaId(  row.getId());
                item.setFundaName(row.getCell().getFunda_name());
                item.setFundaCurrentPrice(fundaCurrentPrice);
                item.setFundaValue(fundaValue);
                item.setDiffValue(diffValue);
                lstFJFundaPO.add(item);
            }
        });
        lstFJFundaPO.sort(comparing(FJFundaPO::getDiffValue));

        //计算是否轮动
        lstFJFundaPO.remove(lstFJFundaPO.size() - 1);//删除军工A //TODO:未来可以参照历史偏差度做轮动（复杂）
        lstFJFundaPO.remove(0);//删除022

        //取最小值
        //TODO
        //FJFundaPO minFJFundaPO = lstFJFundaPO.stream().min(comparing(FJFundaPO::getDiffValue)).get();

        //for (int i = 0; i < FJ_FUNDS_HAVE.length; i++) {
        for(String i : FJ_FUNDS_HAVE){
            if ("150181".equals(i)) {
                continue; //忽略军工A
            }

            final String tmpStr = i;
            List<FJFundaPO> lst = lstFJFundaPO.stream().filter(item -> item.getFundaId().equals(tmpStr)).collect(toList());
            assert (lst.size() == 1);

            //TODO
//            if (lst.get(0).getDiffValue() - minFJFundaPO.getDiffValue() > MAX_DIFF_VALUE) {
//                //                logger.debug("分级" + Integer.toBinaryString(flag));
//                sb.append(String.format("分级A可以做轮动 买入：[%5s %6s][%5.3f]%n", minFJFundaPO.getFundaName(), minFJFundaPO.getFundaId(), minFJFundaPO.getFundaValue()));
//                sb.append(String.format("              卖出：[%5s %6s][%5.3f]%n", lst.get(0).getFundaName(), lst.get(0).getFundaId(), lst.get(0).getFundaValue()));
//
//            }
        }
        this.data=lstFJFundaPO;
    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("--------------分级基金-------------------\n");
        this.data.forEach( item -> sb.append(String.format("%5s  当前价[%5.3f] 净值[%5.3f] 净价[%5.3f] %-4s%n",
                item.getFundaId(), item.getFundaCurrentPrice(),
                item.getFundaValue(), item.getDiffValue(), item.getFundaName())));
        sb.append("-----------------------------------------\n");
        return sb.toString();
    }
}
