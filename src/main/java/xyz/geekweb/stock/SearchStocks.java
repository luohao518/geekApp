package xyz.geekweb.stock;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.geekweb.stock.pojo.FJFundaPO;
import xyz.geekweb.stock.pojo.json.JsonRootBean;
import xyz.geekweb.stock.savesinastockdata.RealTimeData;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;
import xyz.geekweb.util.MailService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 */
@Service
public class SearchStocks {

    public static final double MAX_DIFF_VALUE = 0.003;
    /**
     * 国债逆回购
     */
    final static String[] REVERSE_BONDS = {"sh204001", "sh204002", "sh204003", "sh204004", "sh204007", "sh204014"};
    public static final int LENGTH_1 = REVERSE_BONDS.length;
    final static double MIN_REVERSE_BONDS_VALUE = 4.0;
    /**
     * 货币基金
     */
    final static String[] MONETARY_FUNDS = {"sh511990", "sh511660", "sh511810", "sh511690", "sh511900"};
    public static final int LENGTH_2 = MONETARY_FUNDS.length;
    final static double MAX_MONETARY_FUNDS_VALUE = 99.990;
    /**
     * 股票
     */
    final static String[] STOCKS = {"sh600185", "sh600448", "sh601098", "sz000066", "sz002570", "sh601828", "sh601928", "sh601369", "sz000417"};
    public static final int LENGTH_4 = STOCKS.length;
    final static double MIN_505888_VALUE = 1.013;
    /**
     * 可转债，元和
     */
    final static String[] STOCKS_OTHERS = {"sh132003", "sh110030", "sh505888"};
    public static final int LENGTH_3 = STOCKS_OTHERS.length;
    final static double MAX_STOCKS_PERCENT = 3.0;
    final static double MIN_132003_VALUE = 99.500;
    /**
     * 分级基金
     */
    //sina 接口 final static String[] FJ_FUNDS = {"of150018", "of150171", "of150181", "of150227", "of150200", "of150022"};
    final static String[] FJ_FUNDS = {"150022", "150181", "150018", "150171", "150227", "150200",};
    final static String[] FJ_FUNDS_HAVE = {"150181", "150018"};

    //存储状态
    private int flag;
    private Logger logger = LoggerFactory.getLogger(SearchStocks.class);

    public String doALL() throws IOException {


        flag = 0;

//        logger.debug("flag初始化" + Integer.toBinaryString(flag));

        final String strSinaData = getSinaData();

        final String strJSLData = getJSLData();


        StringBuilder sb = new StringBuilder();
        sb.append(strSinaData);
        sb.append(strJSLData);
        sb.append(String.format("%n%-5s %-5s %-5s %-5s %-5s%n", "国债逆回购", "货币基金", "可转债元和", "股票", "分级基金"));
        String strFlag = StringUtils.reverse(Integer.toBinaryString(flag));

        sb.append(String.format("%-9s %-8s %-8s %-8s %-8s%n", getValue(strFlag, 0), getValue(strFlag, 1), getValue(strFlag, 2), getValue(strFlag, 3), getValue(strFlag, 4)));

        return sb.toString();
    }

    /**
     * 监测 (货币基金，国债逆回购，股票)
     *
     * @return
     */
    public String getSinaData() {

        String[] codes = ArrayUtils.addAll((ArrayUtils.addAll(ArrayUtils.addAll(REVERSE_BONDS, MONETARY_FUNDS), STOCKS_OTHERS)), STOCKS);

        final List<RealTimeDataPOJO> realTimeDataPOJOS = RealTimeData.getRealTimeDataObjects(codes);

        StringBuilder sb = new StringBuilder("\n");
        sb.append("------------------国债逆回购-----------------------\n");


        //国债逆回购判断
        for (int i = 0; i < LENGTH_1; i++) {

            RealTimeDataPOJO item = realTimeDataPOJOS.get(i);
            if (item.getNow() > MIN_REVERSE_BONDS_VALUE) {
                //国债逆回购触发价格高点（对标：浦发天天）
                flag = 1 << 0;
//                logger.debug("国债" + Integer.toBinaryString(flag));
                sb.append(String.format("%5s 当前价[%2.2f] 买入价[%2.2f]%n", item.getName(), item.getNow(), item.getBuy1Pricae()));
            }
        }
        sb.append("-------------------------------------------------\n");

        sb.append("-------------------货币基金------------------------\n");
        //货币基金判断
        for (int i = LENGTH_1; i < LENGTH_1 + LENGTH_2; i++) {

            RealTimeDataPOJO item = realTimeDataPOJOS.get(i);
            if (item.getNow() < MAX_MONETARY_FUNDS_VALUE) {
                //货币基金触发价格低点
                flag = flag | 1 << 1;
                sb.append(String.format("购买货币基金:%s 当前价[%7.3f] 卖出价[%7.3f] 卖量[%5.0f]%n", item.getFullCode(), item.getNow(), item.getSell1Pricae(), item.getSell1Num()));
            }
        }
        sb.append("--------------------------------------------------\n");

        sb.append("-------------------可转债，元和----------------------\n");
        //可转债，元和判断
        for (int i = LENGTH_1 + LENGTH_2; i < LENGTH_1 + LENGTH_2 + LENGTH_3; i++) {

            RealTimeDataPOJO item = realTimeDataPOJOS.get(i);
            if ((item.getFullCode().equals("sh505888") && item.getNow() <= MIN_505888_VALUE)
                    || (item.getFullCode().equals("sh132003") && item.getNow() <= MIN_132003_VALUE)) {

                flag = flag | 1 << 2;
                sb.append(String.format("%-6s 当前价[%7.3f] 卖出价[%7.3f] 卖量[%5.0f] %-6s %n", item.getFullCode(), item.getNow(), item.getSell1Pricae(), item.getSell1Num(), item.getName()));
            }
        }
        sb.append("----------------------------------------------------\n");

        sb.append("-----------------------股票-------------------------\n");
        //股票
        for (int i = LENGTH_1 + LENGTH_2 + LENGTH_3; i < LENGTH_1 + LENGTH_2 + LENGTH_3 + LENGTH_4; i++) {

            RealTimeDataPOJO item = realTimeDataPOJOS.get(i);
            double percent = ((item.getNow() - item.getClose()) / item.getClose()) * 100;
            if (Math.abs(percent) > MAX_STOCKS_PERCENT) {
                //价格突破设定值

                flag = flag | 1 << 3;
//                logger.debug("股票" + Integer.toBinaryString(flag));
                sb.append(String.format("%8s 幅度[%6.2f%%] 当前价[%6.2f] 卖出价[%6.2f] 买入价[%6.2f] %-6s %n", item.getFullCode(), percent, item.getNow(), item.getSell1Pricae(), item.getBuy1Pricae(), item.getName()));
            }
        }
        sb.append("-----------------------股票-------------------------\n");

        return sb.toString();
    }

    /**
     * 监测分级基金数据
     *
     * @return
     */
    public String getJSLData() throws IOException {
        String URL = "https://www.jisilu.cn/data/sfnew/funda_list/";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("服务器端错误: " + response);
        }

        JsonRootBean jsonData = new Gson().fromJson(response.body().string(), JsonRootBean.class);

        List<String> strFjFunds = Arrays.asList(FJ_FUNDS);

        List<FJFundaPO> lstFJFundaPO = new ArrayList<>(10);

        jsonData.getRows().forEach(row -> {

            if (strFjFunds.contains(row.getId())) {

                double fundaCurrentPrice = Double.parseDouble(row.getCell().getFunda_current_price());
                double fundaValue = Double.parseDouble(row.getCell().getFunda_value());
                double diffValue = fundaCurrentPrice - (fundaValue - 1.0);
                FJFundaPO item = new FJFundaPO();
                item.setFundaId(row.getId());
                item.setFundaName(row.getCell().getFunda_name());
                item.setFundaCurrentPrice(fundaCurrentPrice);
                item.setFundaValue(fundaValue);
                item.setDiffValue(diffValue);
                lstFJFundaPO.add(item);

            }
        });

        lstFJFundaPO.sort(comparing(FJFundaPO::getDiffValue));

        StringBuilder sb = new StringBuilder("\n");
        sb.append("-----------------------分级基金-------------------------\n");

        lstFJFundaPO.forEach(item -> {
            sb.append(String.format("%5s  当前价[%5.3f] 净值[%5.3f] 净价[%5.3f] %-4s%n",
                    item.getFundaId(), item.getFundaCurrentPrice(),
                    item.getFundaValue(), item.getDiffValue(), item.getFundaName()));
        });

        //计算是否轮动
        lstFJFundaPO.remove(lstFJFundaPO.size() - 1);//删除军工A //TODO:未来可以参照历史偏差度做轮动（复杂）
        lstFJFundaPO.remove(0);//删除022

        //取最小值
        FJFundaPO minFJFundaPO = lstFJFundaPO.stream().min(comparing(FJFundaPO::getDiffValue)).get();

        for (int i = 0; i < FJ_FUNDS_HAVE.length; i++) {
            if (FJ_FUNDS_HAVE[i].equals("150181")) continue; //忽略军工A

            final String tmpStr = FJ_FUNDS_HAVE[i];
            List<FJFundaPO> lst = lstFJFundaPO.stream().filter(item -> item.getFundaId().equals(tmpStr)).collect(toList());
            assert (lst.size() == 1);

            if (lst.get(0).getDiffValue() - minFJFundaPO.getDiffValue() > MAX_DIFF_VALUE) {
                flag = flag | 1 << 4;
//                logger.debug("分级" + Integer.toBinaryString(flag));
                sb.append(String.format("分级A可以做轮动 买入：[%5s %6s][%5.3f]%n", minFJFundaPO.getFundaName(), minFJFundaPO.getFundaId(), minFJFundaPO.getFundaValue()));
                sb.append(String.format("              卖出：[%5s %6s][%5.3f]%n", lst.get(0).getFundaName(), lst.get(0).getFundaId(), lst.get(0).getFundaValue()));

            }
        }
        sb.append("----------------------------------------------------\n");
        return sb.toString();
    }

    private String getValue(String str, int start) {
        if (StringUtils.isNotEmpty(str)) {
            if (str.length() > start) {
                return str.substring(start, start + 1);
            }
        }
        return "0";
    }

}
