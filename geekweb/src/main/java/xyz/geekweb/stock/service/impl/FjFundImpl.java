//package xyz.geekweb.stock.service.impl;
//
//import com.google.gson.Gson;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.thymeleaf.util.ArrayUtils;
//import xyz.geekweb.config.DataProperties;
//import xyz.geekweb.stock.enums.BuyOrSaleEnum;
//import xyz.geekweb.stock.enums.FinanceTypeEnum;
//import xyz.geekweb.stock.mq.Sender;
//import xyz.geekweb.stock.pojo.json.JsonRootBean;
//import xyz.geekweb.stock.pojo.json.Rows;
//import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeDataPOJO;
//import xyz.geekweb.util.DateUtils;
//
//import java.io.IOException;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//import static java.util.Comparator.comparing;
//import static java.util.stream.Collectors.toList;
//
///**
// * @author lhao
// * @date 2018/4/25
// * 分级基金
// */
//@Service
//public class FjFundImpl implements FinanceData {
//
//
//    //腾讯数据（查询量）
//
//    private static final String QT_URL = "http://qt.gtimg.cn/q=%s";
//
//    //集思录数据
//    private static final String URL = "https://www.jisilu.cn/data/sfnew/funda_list/?___t=%d";
//
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    private List<RealTimeDataPOJO> data;
//
//    private List<RealTimeDataPOJO> watchData = new ArrayList<>();
//
//    private DataProperties dataProperties;
//
//    @Autowired
//    public FjFundImpl(DataProperties dataProperties) {
//        this.dataProperties = dataProperties;
//    }
//
//    /**
//     * init
//     *
//     * @return
//     */
//    public void fetchData() {
//        final List<Rows> rows = fetchJSLData();
//
//        String[] fJFunds = this.dataProperties.getFj_funds().toArray(new String[0]);
//        getQTData(fJFunds);
//        List<String> strFjFunds = Arrays.asList(fJFunds);
//        List<RealTimeDataPOJO> lstDataPO = new ArrayList<>(10);
//        rows.forEach(row -> {
//            if (strFjFunds.contains(row.getId())) {
//                double fundaCurrentPrice = Double.parseDouble(row.getCell().getFunda_current_price());
//                double fundaValue = Double.parseDouble(row.getCell().getFunda_value());
//                //净价
//                double trueValue = fundaCurrentPrice - (fundaValue - 1.0);
//                RealTimeDataPOJO item = new RealTimeDataPOJO();
//                item.setSearchType(FinanceTypeEnum.FJ_FUND);
//                item.setFullCode(row.getId());
//                item.setName(row.getCell().getFunda_name());
//                item.setNow(fundaCurrentPrice);
//                item.setValue(fundaValue);
//                item.setTrueValue(trueValue);
//                item.setBuyOrSaleEnum(BuyOrSaleEnum.BUY);
//                item.setRiseAndFallPercent(Double.parseDouble(StringUtils.remove(row.getCell().getFunda_increase_rt(), '%')));
//                String last_time = row.getCell().getLast_time();
//                LocalTime dt = LocalTime.parse(last_time, DateTimeFormatter.ISO_LOCAL_TIME);
//                item.setTime(DateUtils.asDate(dt));
//                lstDataPO.add(item);
//            }
//        });
//
//        //按照净价从低到高排序
//        lstDataPO.sort(comparing(RealTimeDataPOJO::getTrueValue));
//
//        List<String> fj_funds_have = this.dataProperties.getFj_funds_have();
//        for (String i : fj_funds_have) {
//
//            //取出持有的项目
//            final String tmpStr = i;
//            List<RealTimeDataPOJO> lst = lstDataPO.stream().filter(item -> item.getFullCode().equals(tmpStr)).collect(toList());
//            assert (lst.size() == 1);
//            RealTimeDataPOJO haveItem = lst.get(0);
//            haveItem.setBuyOrSaleEnum(BuyOrSaleEnum.SALE);
//
//            //最低价的项目
//            RealTimeDataPOJO lowestItem = lstDataPO.get(0);
//            if ("150022".equalsIgnoreCase(lowestItem.getFullCode())) {
//                lowestItem = lstDataPO.get(1);
//            }
//            //阈值
//            final double fj_min_diff = Double.parseDouble(dataProperties.getMap().get("FJ_MIN_DIFF"));
//
//            if ((haveItem.getTrueValue() - lowestItem.getTrueValue()) >= fj_min_diff) {
//                //持有的分级净价比最低的分级净价大
//
//                if ("150181".equals(i)) {
//                    if ((haveItem.getTrueValue() - 0.12d - lowestItem.getTrueValue()) >= fj_min_diff) {
//                        //军工A的场合，净价计算减1.2分钱
//                        this.watchData.add(haveItem);
//                    }
//                } else {
//                    this.watchData.add(haveItem);
//                }
//            }
//        }
//        if (this.watchData.size() > 0) {
//            if ("150022".equalsIgnoreCase(lstDataPO.get(0).getFullCode())) {
//                this.watchData.add(lstDataPO.get(1));
//            } else {
//                this.watchData.add(lstDataPO.get(0));
//            }
//        }
//        this.data = lstDataPO;
//    }
//
//    private List<Rows> fetchJSLData() {
//        OkHttpClient client = new OkHttpClient();
//        String url = String.format(URL, System.currentTimeMillis());
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//        logger.debug(url);
//        Response response;
//        try {
//            response = client.newCall(request).execute();
//        } catch (IOException e) {
//            throw new RuntimeException("服务器端错误: ", e);
//        }
//        if (!response.isSuccessful()) {
//            throw new RuntimeException("服务器端错误: " + response.message());
//        }
//        JsonRootBean jsonData;
//        try {
//            jsonData = new Gson().fromJson(response.body().string(), JsonRootBean.class);
//        } catch (IOException e) {
//            throw new RuntimeException("服务器端错误: ", e);
//        }
//        return jsonData.getRows();
//    }
//
//    private void getQTData(final String[] fJFunds) {
//
//        StringBuilder sb;
//        String[] tmpArray = ArrayUtils.copyOf(fJFunds, fJFunds.length);
//        for (int i = 0; i < tmpArray.length; i++) {
//            sb = new StringBuilder();
//            tmpArray[i] = sb.append("sz").append(fJFunds[i]).toString();
//        }
//        fetchQTData(StringUtils.join(tmpArray, ","));
//    }
//
//    private Map<String, String[]> fetchQTData(String strLst) {
//        logger.debug("fetchQTData[{}]", strLst);
//        OkHttpClient client = new OkHttpClient();
//        String url = String.format(QT_URL, strLst);
//        logger.debug(url);
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//        Response response;
//        try {
//            response = client.newCall(request).execute();
//        } catch (IOException e) {
//            throw new RuntimeException("服务器端错误: ", e);
//        }
//        if (!response.isSuccessful()) {
//            throw new RuntimeException("服务器端错误: " + response.message());
//        }
//        try {
//            String result = response.body().string();
//            logger.debug("result[{}]", result);
//            String[] datas = StringUtils.split(result, ";");
//            Map<String, String[]> dataMap = new HashMap(10);
//            for (String item : datas) {
//                if (StringUtils.isEmpty(StringUtils.trim(item))) {
//                    break;
//                }
//                String[] strSplit = StringUtils.split(item, "=");
//                String substring = strSplit[0].substring(3);
//                dataMap.put(substring, StringUtils.split(strSplit[1], "~"));
//            }
//            return dataMap;
//        } catch (IOException e) {
//            throw new RuntimeException("服务器端错误: ", e);
//        }
//    }
//
//    @Override
//    public void printInfo() {
//
//        StringBuilder sb = new StringBuilder("\n");
//        sb.append("--------------分级基金-------------------\n");
//        this.data.forEach(item -> sb.append(String.format("%5s  当前价[%5.3f] 净值[%5.3f] 净价[%5.3f] %-4s%n",
//                item.getFullCode(), item.getNow(),
//                item.getValue(), item.getTrueValue(), item.getName())));
//
//        sb.append("-----------------------------------------\n");
//        logger.info(sb.toString());
//    }
//
//    @Override
//    public void sendNotify(Sender sender) {
//        sender.sendNotify(this.watchData);
//    }
//
//    @Override
//    public List<RealTimeDataPOJO> getData() {
//        return this.data;
//    }
//}
