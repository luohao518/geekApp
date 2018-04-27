package xyz.geekweb.stock.impl;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.thymeleaf.util.ArrayUtils;
import xyz.geekweb.stock.FinanceData;
import xyz.geekweb.stock.pojo.FJFundaPO;
import xyz.geekweb.stock.pojo.json.Rows;

import java.io.IOException;
import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * @author lhao
 * @date 2018/4/25
 * 分级基金
 */
public class FjFundImpl implements FinanceData {

    private final static String[] FJ_FUNDS = {"150022", "150181", "150018", "150171", "150227", "150200",};
    private final static String[] FJ_FUNDS_HAVE = {"150181", "150018"};
    private final static double MAX_DIFF_VALUE = 0.003;
    private static final String QT_URL = "http://qt.gtimg.cn/q=%s";
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<FJFundaPO> data;

    public FjFundImpl(List<Rows> rows) {
        this.data = initData(rows);
    }

    private List<FJFundaPO> initData(List<Rows> rows) {
        getQTData(FJ_FUNDS);
        List<String> strFjFunds = Arrays.asList(FJ_FUNDS);
        List<FJFundaPO> lstFJFundaPO = new ArrayList<>(10);
        rows.forEach(row -> {
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

        //计算是否轮动
        //删除军工A //TODO:未来可以参照历史偏差度做轮动（复杂）
        lstFJFundaPO.remove(lstFJFundaPO.size() - 1);
        //删除022
        lstFJFundaPO.remove(0);

        for (String i : FJ_FUNDS_HAVE) {
            if ("150181".equals(i)) {
                //忽略军工A
                continue;
            }

            final String tmpStr = i;
            List<FJFundaPO> lst = lstFJFundaPO.stream().filter(item -> item.getFundaId().equals(tmpStr)).collect(toList());
            assert (lst.size() == 1);
        }
        return lstFJFundaPO;
    }

    private void getQTData(final String[] fJFunds) {

        StringBuilder sb;
        String[] tmpArray = ArrayUtils.copyOf(fJFunds, fJFunds.length);
        for (int i = 0; i < tmpArray.length; i++) {
            sb = new StringBuilder();
            tmpArray[i] = sb.append("sz").append(fJFunds[i]).toString();
        }
        fetchQTData(StringUtils.join(tmpArray, ","));
    }

    private Map<String, String[]> fetchQTData(String strLst) {
        logger.debug("fetchQTData[{}]", strLst);
        OkHttpClient client = new OkHttpClient();
        String url = String.format(QT_URL, strLst);
        logger.debug(url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException("服务器端错误: ", e);
        }
        if (!response.isSuccessful()) {
            throw new RuntimeException("服务器端错误: " + response.message());
        }
        try {
            String result = response.body().string();
            logger.debug("result[{}]", result);
            String[] datas = StringUtils.split(result, ";");
            Map<String, String[]> dataMap = new HashMap(10);
            for (String item : datas) {
                if (StringUtils.isEmpty(StringUtils.trim(item))) {
                    break;
                }
                String[] strSplit = StringUtils.split(item, "=");
                String substring = strSplit[0].substring(3);
                dataMap.put(substring, StringUtils.split(strSplit[1], "~"));
            }
            return dataMap;
        } catch (IOException e) {
            throw new RuntimeException("服务器端错误: ", e);
        }
    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("--------------分级基金-------------------\n");
        this.data.forEach(item -> sb.append(String.format("%5s  当前价[%5.3f] 净值[%5.3f] 净价[%5.3f] %-4s%n",
                item.getFundaId(), item.getFundaCurrentPrice(),
                item.getFundaValue(), item.getDiffValue(), item.getFundaName())));

        //取最小值
        FJFundaPO minFJFundaPO = this.data.stream().min(comparing(FJFundaPO::getDiffValue)).get();

        if (this.data.get(0).getDiffValue() - minFJFundaPO.getDiffValue() > MAX_DIFF_VALUE) {

            logger.warn(String.format("分级A可以做轮动 买入：[%5s %6s][%5.3f]%n", minFJFundaPO.getFundaName(), minFJFundaPO.getFundaId(), minFJFundaPO.getFundaValue()));
            logger.warn(String.format("              卖出：[%5s %6s][%5.3f]%n", this.data.get(0).getFundaName(), this.data.get(0).getFundaId(), this.data.get(0).getFundaValue()));

        }
        sb.append("-----------------------------------------\n");
        return sb.toString();
    }
}
