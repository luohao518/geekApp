package xyz.geekweb.stock;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.geekweb.stock.pojo.savesinastockdata.Tools;

import java.io.IOException;

public class DoNet {

    private static Logger logger = LoggerFactory.getLogger(DoNet.class);

    public static void doPost(String name) {
        OkHttpClient client = new OkHttpClient();

        //curl -d "email=1231@qq.com&pass=123456" https://www.import-express.com/reg/reg

        String email = name + "abc@qq.com";
        RequestBody formBody =null; /*new FormEncodingBuilder()
                .add("email", email)
                .add("pass", "123456")
                .build();*/

        Request request = new Request.Builder()
                .url("https://www.import-express.com/reg/reg")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                if (response.body().string().equals("1")) {
                    System.out.println("成功建立一个账户:" + email);
                } else {
                    System.err.println("失败:" + email);
                }
            } else {
                throw new IOException("Unexpected code " + response);
            }
            // Do something with the response.
        } catch (IOException e) {
            logger.error("doPost",e);
        }
    }

}
