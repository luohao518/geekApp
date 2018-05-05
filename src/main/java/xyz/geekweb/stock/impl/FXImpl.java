package xyz.geekweb.stock.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import xyz.geekweb.stock.FinanceData;
import xyz.geekweb.stock.pojo.json.FXBean;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lhao
 * @date 2018/4/27
 * <p>
 * 外汇
 */
@Service
public class FXImpl implements FinanceData {


    private static final String DATA_URL = "https://forex.1forge.com/1.0.3/quotes?pairs=%s&api_key=iOrFNzxp8Fuus91yAMYRO7nTkSImR5Gm";
    private static final String MARKET_STATUS = "https://forex.1forge.com/1.0.3/market_status?api_key=iOrFNzxp8Fuus91yAMYRO7nTkSImR5Gm";
    private static final String QUOTA = "https://forex.1forge.com/1.0.3/quota?api_key=iOrFNzxp8Fuus91yAMYRO7nTkSImR5Gm";
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<FXBean> data;
    private List<FXBean> watchData =new ArrayList<>();

    public FXImpl() {

    }

    public void initData(String[] fxs) {
        if (isRemaining()) {
            this.data=fetchData(StringUtils.join(fxs, ","));
        } else {
            logger.warn("今天调用API次数到，明天再试");
        }
    }

    @Override
    public boolean isNotify(){
        return this.watchData!=null && this.watchData.size()>0;
    }

    private List<FXBean> fetchData(String strLst) {
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
        } catch (IOException e) {
            throw new RuntimeException("服务器端错误: ", e);
        }

        return fXBeans;

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

    @Override
    public String toPrintout() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("--------------外汇-----------------\n");
        if (this.data != null) {
            if (!isOpen()) {
                sb.append("!!!已休市!!!\n");
            }
            this.data.forEach(item -> {
                sb.append(String.format("外汇购买:%s 当前价[%7.3f]%n", item.getSymbol(), item.getPrice()));
                if(item.getSymbol().equals("USDJPY")){
                    if(item.getPrice()>112.0d){
                        this.watchData.add(item);
                    }
                }
            });
        }
        sb.append("------------------------------------\n");
        return sb.toString();
    }
}
