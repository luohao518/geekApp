package xyz.geekweb.jsoup;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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
public class AlmanacUtil2 {

    /**
     * 单例工具类
     */
    private AlmanacUtil2() {
    }

    /**
     * 获取万年历信息
     *
     * @return
     */
    public static String getToken() {
        String url = "http://data.eastmoney.com/hsgtcg/StockHdStatistics.aspx?stock=600185";
        String html = pickData(url);
        return analyzeHTMLByString(html);
    }

    /*
     * 爬取网页信息
     */
    private static String pickData(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                // 打印响应状态
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
     * 使用jsoup解析网页信息
     */
    private static String analyzeHTMLByString(String html) {

        Document document = Jsoup.parse(html);

        /*取得script下面的JS变量*/
        Elements elements = document.getElementsByTag("script");
        Elements e = elements.eq(22);
        /*循环遍历script下面的JS变量*/
        for (Element element : e) {
            /*取得JS变量数组*/
            String[] data = element.data().split("var");
            /*取得单个JS变量*/
            for (String variable : data) {
                /*过滤variable为空的数据*/
                if (variable.contains("=")) {
                    /*取到满足条件的JS变量*/
                    if (variable.contains("list")) {
                        String[] kvp = variable.split("=");
                        return kvp[3].substring(0,kvp[3].indexOf('&'));
                    }
                }
            }
        }

        return null;
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
