package xyz.geekweb.util;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author lhao
 */
public class HolidayUtil {

    private static Logger logger = LoggerFactory.getLogger(HolidayUtil.class);

    /**
     * 查询是否为节假日API接口  工作日对应结果为 0, 休息日对应结果为 1
     */
    private static String URL = "http://149.28.30.85:3000/hd?d=%s";

    private static int[] STOCK_TIMES = new int[]{915, 1130, 1300, 1500};

    public static boolean isStockTimeEnd() {
        int intHHMM = Integer.parseInt(LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm")));
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


    public static boolean isHoliday()  {
        try {
            return isHoliday(LocalDate.now());
        } catch (IOException e) {
            logger.error("isHoliday:",e);
            return false;
        }
    }

    public static boolean isHoliday(LocalDate date) throws IOException {

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(String.format(URL, date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            logger.error("服务器端错误: " + response);
            return true;
        }

        String result = response.body().string();
        String s = JSON.parseObject(result).get("result").toString();
        logger.debug("result:"+ s);

        return s.equals("0") ? false : true;
    }

    public static boolean isHoliday(String str) throws IOException {
        return isHoliday(LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyyMMdd")));

    }

}
