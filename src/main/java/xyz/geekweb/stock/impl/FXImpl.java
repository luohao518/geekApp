package xyz.geekweb.stock.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
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
public class FXImpl implements FinanceData {

    private final static String[] FX_ARRAY = {"USDJPY", ",EURUSD", "AUDUSD"};
    private static final String DATA_URL = "https://forex.1forge.com/1.0.3/quotes?pairs=%s&api_key=iOrFNzxp8Fuus91yAMYRO7nTkSImR5Gm";
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<FXBean> data;

    public FXImpl() {
        this.data = initData();
    }

    private List<FXBean> initData() {
        return fetchData(StringUtils.join(FX_ARRAY, ","));
    }


    private List<FXBean> fetchData(String strLst) {
        logger.debug("fetchData[{}]", strLst);
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

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("--------------外汇-----------------\n");

        this.data.forEach(item -> sb.append(String.format("外汇购买:%s 当前价[%7.3f]%n", item.getSymbol(), item.getPrice())));
        sb.append("-----------------------------------------\n");
        return sb.toString();
    }
}
