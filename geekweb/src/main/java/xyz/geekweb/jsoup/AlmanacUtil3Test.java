package xyz.geekweb.jsoup;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author lhao
 * @date 2020/7/5
 */
public class AlmanacUtil3Test {

    public static void main(String args[]) throws IOException {

        String token = AlmanacUtil2.getToken();
        bean1[] jsonData = AlmanacUtil3.getJsonData(token, "002614");
        System.out.println("-----------------");
        System.out.println(Arrays.toString(jsonData));

    }
}
