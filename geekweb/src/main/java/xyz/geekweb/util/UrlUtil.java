package xyz.geekweb.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.ocean.rawsdk.util.PropertyUtils;
import com.google.common.base.CharMatcher;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author luohao
 * @date 2019/12/5
 */
@Slf4j
public class UrlUtil {


    public static final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    /**
     * singleton
     */
    private static UrlUtil singleton = null;

    /**
     * The singleton HTTP client.
     */
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    private final OkHttpClient clientLongTime = new OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .build();

    /**
     * 构造函数
     */
    private UrlUtil() {

    }

    /**
     * getInstance
     *
     * @return
     */
    public static UrlUtil getInstance() {

        if (singleton == null) {
            synchronized (UrlUtil.class) {
                if (singleton == null) {
                    singleton = new UrlUtil();
                }
            }
        }
        return singleton;
    }


    /**
     * get调用（有重试机制，默认15次重试，每次1秒）
     * @param url
     * @return
     * @throws IOException
     */
    public JSONObject callUrlByGet(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return executeCall(url, request);
    }

    public String callUrlByGetReturnString(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return executeCallReturnString(url, request);
    }

    /**
     * Post调用
     *
     * @param url
     * @return
     * @throws IOException
     */
    public JSONObject callUrlByPost(String url, Object param) throws IOException {
        String param_ = JSONObject.toJSONString(param);
        RequestBody requestBody = RequestBody.create(mediaType, param_);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        return executeCall(url, request);
    }

    /**
     * Post调用
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject postURL(String url, Map<String, String> params) throws IOException {

        // Create okhttp3 form body builder.
        FormBody.Builder bodyBuilder = new FormBody.Builder();

        // Add form parameters
        params.forEach((k, v) -> {
            if (v != null) bodyBuilder.add(k, v);
        });

        // Build form body.
        FormBody body = bodyBuilder.build();

        // Create a http request object.
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return executeCall(url, request);
    }

    public JSONObject callUrlByPut(String url, Map<String, String> params) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        params.forEach((k, v) -> {
            if (v != null) builder.add(k, v);
        });
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        return executeCall(url, request);

    }

    /**
     * Delete调用
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject doDelete(String url, Map<String, String> params) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        return executeCall(url, request);
    }

    /**
     * Patch调用
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public JSONObject doPatch(String url, Map<String, String> params) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        params.forEach((k, v) -> {
            if (v != null) builder.add(k, v);
        });
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .build();
        return executeCall(url, request);
    }

    /**
     * call url by retry times
     * @param url
     * @param request
     * @return
     * @throws IOException
     */
    private JSONObject executeCall(String url, Request request) throws IOException {
        Response response = getResponse(url, request);

        return response.body() != null ?
                JSON.parseObject(response.body().string()) : null;
    }

    private String executeCallReturnString(String url, Request request) throws IOException {
        Response response = getResponse(url, request);

        return response.body() != null ?
                response.body().string() : null;
    }

    private Response getResponse(String url, Request request) throws IOException {
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException ioe) {
            //重试15次（每次1秒）
            try {
                int count = 0;
                while (true) {
                    Thread.sleep(1000);
                    try {
                        response = client.newCall(request).execute();
                    } catch (IOException e) {
                        log.warn("do retry ,times=[{}]", count);
                    }
                    if (count > 15) {
                        break;
                    }
                    ++count;
                }
            } catch (InterruptedException e) {
            }
        }

        if (response == null || !response.isSuccessful()) {
            log.error("url:[{}]", url);
            throw new IOException("call url is not successful");
        }
        return response;
    }

    /**
     * 图片搜索使用
     *
     * @param originFile
     * @param paramFileName
     * @param accessUrl
     * @param page
     * @return
     * @throws IOException
     */
    public JSONObject postFile(File originFile, String paramFileName, String accessUrl, Integer page) throws IOException {
        String imageType = "image/jpeg";
        if (originFile.getName().endsWith(".png")) {
            imageType = "image/png";
        }
        System.err.println(accessUrl);
        RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(paramFileName, originFile.getName() + "##" + page,
                        RequestBody.create(MediaType.parse(imageType), originFile)).build();
        Request request = new Request.Builder().url(accessUrl).post(formBody).build();
        Response response = clientLongTime.newCall(request).execute();
        if (!response.isSuccessful()) {
            log.error("originFile:[{}],url:[{}],postFile error", originFile, accessUrl);
            throw new IOException("response is not successful");
        }
        return response.body() != null ?
                JSON.parseObject(response.body().string()) : null;
    }

    public JSONObject postFile(File originFile, String paramFileName, String accessUrl) throws IOException {
        String imageType = "image/jpeg";
        if (originFile.getName().endsWith(".png")) {
            imageType = "image/png";
        }
        System.err.println("accessUrl:" + accessUrl);
        RequestBody formBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart(paramFileName, originFile.getName(),
                        RequestBody.create(MediaType.parse(imageType), originFile)).build();
        Request request = new Request.Builder().url(accessUrl).post(formBody).build();
        Response response = clientLongTime.newCall(request).execute();
        if (!response.isSuccessful()) {
            log.error("originFile:[{}],url:[{}],postFile error", originFile, accessUrl);
            throw new IOException("response is not successful");
        }
        return response.body() != null ?
                JSON.parseObject(response.body().string()) : null;
    }


}
