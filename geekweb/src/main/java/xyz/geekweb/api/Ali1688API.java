package xyz.geekweb.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author luohao
 * @date 2019/11/4
 */
@Slf4j
public class Ali1688API {

    /**
     * 获取商品详情
     */
    private static final String URL = "http://api.onebound.cn/1688/api_call.php?num_iid=%s&cache=no&api_name=item_get&lang=en&key=tel13661551626&secret=20191104";

    /**
     * singleton
     */
    private static Ali1688API singleton = null;

    /**
     * The singleton HTTP client.
     */
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    /**
     * 构造函数
     */
    private Ali1688API() {

    }

    /**
     * getInstance
     * @return
     */
    public static Ali1688API getInstance() {

        if (singleton == null) {
            synchronized (Ali1688API.class) {
                if (singleton == null) {
                    singleton = new Ali1688API();
                }
            }
        }
        return singleton;
    }

    /**
     * 1688商品详情查询
     * @param pid
     * @return
     */
    public JSONObject getItem(long pid) {

        try {
            JSONObject jsonObject = callURLByGet(String.format(URL, pid));
            String error = jsonObject.getString("error");
            if (StringUtils.isNotEmpty(error)) {
                log.warn("The pid:[{}] is not invalid.", pid);
                return null;
            } else {
                return jsonObject;
            }
        } catch (IOException e) {
            log.error("getItem", e);
            return null;
        }
    }

    /**
     * 调用URL（Get）
     * @param URL
     * @return
     * @throws IOException
     */
    private JSONObject callURLByGet(String URL) throws IOException {

        Request request = new Request.Builder().url(URL).build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("response is not successful");
        }
        return JSON.parseObject(response.body().string());
    }

}
