package xyz.geekweb.jsoup;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <STRONG>类描述</STRONG> :  2345万年历信息爬取工具<p>
 *
 * @author 溯源blog
 *
 * <STRONG>创建时间</STRONG> : 2016年4月11日 下午14:15:44<p>
 * <STRONG>修改历史</STRONG> :<p>
 * <pre>
 * 修改人                   修改时间                     修改内容
 * ---------------         -------------------         -----------------------------------
 * </pre>
 * @version 1.0 <p>
 */
public class AlmanacUtil3 {

    /**
     * 单例工具类
     */
    private AlmanacUtil3() {
    }

    /**
     * 获取万年历信息
     *
     * @return
     */
    public static bean1[] getJsonData(String token,String stockCode) throws IOException {
        String url = "http://dcfm.eastmoney.com//em_mutisvcexpandinterface/api/js/get?type=HSGTHDSTA&token=%s&filter=(SCODE='%s')&st=HDDATE&sr=-1&p=1&ps=50&js=var yKYRKGdC={pages:(tp),data:(x)}&rt=53130646";
        String html = pickData(String.format(url,token,stockCode));
        return analyzeHTMLByString(html);
    }

    /*
     * 爬取网页信息
     */
    private static String pickData(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String encodeUrl = URLEncoder.encode(url);
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
        ResponseBody body = response.body();
        System.out.println(body);
        return body.string();


    }

    /*
     * 使用jsoup解析网页信息
     */
    private static bean1[] analyzeHTMLByString(String html) {
//        var yKYRKGdC={pages:1,data:[{"HDDATE":"2020-07-03T00:00:00","HKCODE":"1000008049","SCODE":"002614","SNAME":"奥佳华","SHAREHOLDSUM":7912904.0,"SHARESRATE":1.4,"CLOSEPRICE":10.96,"ZDF":-3.606,"SHAREHOLDPRICE":86725427.84,"SHAREHOLDPRICEONE":22012845.440000013,"SHAREHOLDPRICEFIVE":38845979.620000005,"SHAREHOLDPRICETEN":61352411.440000005,"MARKET":"003","ShareHoldSumChg":2221384.0,"Zb":0.023553766740083609,"Zzb":0.014098652567249147},{"HDDATE":"2020-07-02T00:00:00","HKCODE":"1000008049","SCODE":"002614","SNAME":"奥佳华","SHAREHOLDSUM":5691520.0,"SHARESRATE":1.01,"CLOSEPRICE":11.37,"ZDF":2.8959,"SHAREHOLDPRICE":64712582.399999991,"SHAREHOLDPRICEONE":25884289.339999989,"SHAREHOLDPRICEFIVE":35435811.839999989,"SHAREHOLDPRICETEN":32657942.729999989,"MARKET":"003","ShareHoldSumChg":2152022.0,"Zb":0.01694153429341751,"Zzb":0.010140747702682839}]}
        String json = html.substring(html.indexOf("data:")+5,html.length()-1);

        return new Gson().fromJson(json, bean1[].class);
    }

    public static void  main(String[] args){
        String tmp="var yKYRKGdC={pages:1,data:[{\"HDDATE\":\"2020-07-03T00:00:00\",\"HKCODE\":\"1000008049\",\"SCODE\":\"002614\",\"SNAME\":\"奥佳华\",\"SHAREHOLDSUM\":7912904.0,\"SHARESRATE\":1.4,\"CLOSEPRICE\":10.96,\"ZDF\":-3.606,\"SHAREHOLDPRICE\":86725427.84,\"SHAREHOLDPRICEONE\":22012845.440000013,\"SHAREHOLDPRICEFIVE\":38845979.620000005,\"SHAREHOLDPRICETEN\":61352411.440000005,\"MARKET\":\"003\",\"ShareHoldSumChg\":2221384.0,\"Zb\":0.023553766740083609,\"Zzb\":0.014098652567249147},{\"HDDATE\":\"2020-07-02T00:00:00\",\"HKCODE\":\"1000008049\",\"SCODE\":\"002614\",\"SNAME\":\"奥佳华\",\"SHAREHOLDSUM\":5691520.0,\"SHARESRATE\":1.01,\"CLOSEPRICE\":11.37,\"ZDF\":2.8959,\"SHAREHOLDPRICE\":64712582.399999991,\"SHAREHOLDPRICEONE\":25884289.339999989,\"SHAREHOLDPRICEFIVE\":35435811.839999989,\"SHAREHOLDPRICETEN\":32657942.729999989,\"MARKET\":\"003\",\"ShareHoldSumChg\":2152022.0,\"Zb\":0.01694153429341751,\"Zzb\":0.010140747702682839}]}";

        analyzeHTMLByString(tmp);
    }


    /*
     * 获取忌/宜
     */
    private static String getSuggestion(Document doc, String id) {
        Element element = doc.getElementById(id);
        Elements elements = element.getElementsByTag("a");
        StringBuffer sb = new StringBuffer();
        for (Element e : elements) {
            sb.append(e.text() + " ");
        }
        return sb.toString();
    }

    /*
     * 获取公历时间,用yyyy年MM月dd日 EEEE格式表示。
     * @return yyyy年MM月dd日 EEEE
     */
    private static String getSolarDate() {
        Calendar calendar = Calendar.getInstance();
        Date solarDate = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 EEEE");
        return formatter.format(solarDate);
    }

}
