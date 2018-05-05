package xyz.geekweb.util;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author lhao
 */
public class HolidayUtil {

    /**
     * 查询是否为节假日API接口  工作日对应结果为 0, 休息日对应结果为 1, 节假日对应的结果为 2
     */
    private static String URL = "http://tool.bitefu.net/jiari?d=%s";

    private static int[] STOCK_TIMES = new int[]{915, 1130, 1300, 1500};

    public static boolean isStockTimeEnd() {
        int intHHMM = Integer.parseInt(LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm")));
        System.out.println(intHHMM);
        if (intHHMM > STOCK_TIMES[3]) {
            return true;
        }
        return false;
    }

    public static boolean isStockTime() {

        return isStockTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm")));
    }

    public static boolean isStockTime(String hhmm) {

        if (StringUtils.isEmpty(hhmm)) {
            throw new java.lang.IllegalArgumentException();
        }

        int intHHMM = Integer.parseInt(hhmm);
        if ((intHHMM >= STOCK_TIMES[0] && intHHMM <= STOCK_TIMES[1]) || (intHHMM >= STOCK_TIMES[2] && intHHMM <= STOCK_TIMES[3])) {
            return true;
        } else {
            return false;
        }

    }


    public static boolean isHoliday() throws IOException {
        return isHoliday(LocalDate.now());
    }

    public static boolean isHoliday(LocalDate date) throws IOException {

        System.out.println(date.toString());
        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(String.format(URL, date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))))
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("服务器端错误: " + response);
        }

        String result = response.body().string();

        return result.equals("0") ? false : true;
    }

    public static boolean isHoliday(String str) throws IOException {
        return isHoliday(LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyyMMdd")));

    }

}
