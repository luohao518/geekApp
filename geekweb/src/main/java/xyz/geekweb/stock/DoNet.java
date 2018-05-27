package xyz.geekweb.stock;

import com.squareup.okhttp.*;

import java.io.IOException;

public class DoNet {

    public  static void doPost(String name){
        OkHttpClient client = new OkHttpClient();

        //curl -d "email=1231@qq.com&pass=123456" https://www.import-express.com/reg/reg

        String email=name+"abc@qq.com";
        RequestBody formBody = new FormEncodingBuilder()
                .add("email", email)
                .add("pass","123456")
                .build();

        Request request = new Request.Builder()
                .url("https://www.import-express.com/reg/reg")
                .post(formBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                if (response.body().string().equals("1")) {
                    System.out.println("成功建立一个账户:"+email);
                }else{
                    System.err.println("失败:"+email);
                }
            } else {
                throw new IOException("Unexpected code " + response);
            }
            // Do something with the response.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}