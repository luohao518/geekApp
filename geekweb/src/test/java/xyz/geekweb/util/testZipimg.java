package xyz.geekweb.util;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.stream.IntStream;

/**
 * @author luohao
 * @date 2018/7/12
 */
public class testZipimg {

    public static void main(String... args) throws IOException {
        OkHttpClient client = new OkHttpClient();
       /* Request request = new Request.Builder()
                .url("http://104.247.194.50:3000/zipImage?paths=/usr/local/goodsimg/importcsvimg/singleimg/543639372192/7292350030_1240275881.400x400.jpg&type=2")
                .build();
        try {
            Response response = client.newCall(request).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        IntStream.range(0, 100).forEach(item -> {
            Request request = new Request.Builder()
                    .url("http://104.247.194.50:3000/zipImage?paths=/usr/local/goodsimg/importcsvimg/singleimg/543639372192/7292350030_1240275881.400x400.jpg&type=2")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
