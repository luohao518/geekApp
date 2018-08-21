package xyz.geekweb;



import okhttp3.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadImage {

    public void upload(String url, File file) throws IOException {
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), file))
                        //RequestBody.create(MediaType.parse("image/png"), file))
                .addFormDataPart("token", "XXXXXXXXXX")
                .addFormDataPart("destPath", "/usr/local/goodsimg/importcsvimg/test/36996699/")
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

    public static void main(String[] args) throws IOException {
        UploadImage up = new UploadImage();
        up.upload("http://XXXXXXXXXXXXX/uploadImage",new File("D:\\work\\myNodejs\\imgs\\1\\111.jpg"));
    }

}