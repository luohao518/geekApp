package xyz.geekweb.stock.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import xyz.geekweb.stock.mq.Sender;
import xyz.geekweb.stock.pojo.json.FXBean;
import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeDataPOJO;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lhao
 * @date 2018/4/27
 * <p>
 * 外汇
 * 免费版本每天请求次数上限1000次
 */
@Service
public class FXImpl implements FinanceData {


    private static final String DATA_URL = "https://forex.1forge.com/1.0.3/quotes?pairs=%s&api_key=iOrFNzxp8Fuus91yAMYRO7nTkSImR5Gm";
    private static final String MARKET_STATUS = "https://forex.1forge.com/1.0.3/market_status?api_key=iOrFNzxp8Fuus91yAMYRO7nTkSImR5Gm";
    private static final String QUOTA = "https://forex.1forge.com/1.0.3/quota?api_key=iOrFNzxp8Fuus91yAMYRO7nTkSImR5Gm";
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<RealTimeDataPOJO> data;

    private List<RealTimeDataPOJO> watchData = new ArrayList<>();

    public void fetchData(String[] fxs) {
        if (isRemaining()) {
            this.data = fetchData(StringUtils.join(fxs, ","));
        } else {
            logger.warn("今天调用API次数到，明天再试");
        }
    }

    private boolean isRemaining() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(QUOTA)
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

            JSONObject map = JSON.parseObject(response.body().string());
            return map.getInteger("quota_remaining") > 0;
        } catch (IOException e) {
            throw new RuntimeException("服务器端错误: ", e);
        }


    }

    private List<RealTimeDataPOJO> fetchData(String strLst) {
        OkHttpClient client = new OkHttpClient();
        String url = String.format(DATA_URL, strLst);
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

        List<FXBean> fXBeans;
        try {
            Type listType = new TypeToken<ArrayList<FXBean>>() {
            }.getType();
            fXBeans = new Gson().fromJson(response.body().string(), listType);
            //set date
            fXBeans.stream().forEach(item -> item.setTime(new Date(item.getTimestamp() * 1000)));
        } catch (IOException e) {
            throw new RuntimeException("服务器端错误: ", e);
        }

        List<RealTimeDataPOJO> result = new ArrayList<>(fXBeans.size());
        fXBeans.forEach(bean -> {
            RealTimeDataPOJO item = new RealTimeDataPOJO();
            item.setName(bean.getSymbol());
            item.setNow(bean.getPrice());
            item.setTime(bean.getTime());
            result.add(item);
        });
        return result;

    }

    @Override
    public void printInfo() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("--------------外汇-----------------\n");
        if (this.data != null) {
            if (!isOpen()) {
                sb.append("!!!已休市!!!\n");
            }
            this.data.forEach(item -> {
                sb.append(String.format("外汇购买:%s 当前价[%7.3f]%n", item.getName(), item.getNow()));
                if (item.getName().equals("USDJPY")) {
                    if (item.getNow() > 112.0d) {
                        this.watchData.add(item);
                    }
                }
            });
        }
        sb.append("------------------------------------\n");
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

    private boolean isOpen() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(MARKET_STATUS)
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

            JSONObject map = JSON.parseObject(response.body().string());
            return "true".equalsIgnoreCase(map.getString("market_is_open"));
        } catch (IOException e) {
            throw new RuntimeException("服务器端错误: ", e);
        }
    }
}
