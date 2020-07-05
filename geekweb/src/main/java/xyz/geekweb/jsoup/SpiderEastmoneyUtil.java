package xyz.geekweb.jsoup;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class SpiderEastmoneyUtil {

    /** 北向资金流入股票-流通股占比排序*/
    private final static String URL_HSGT20_GGTJ_SUM="http://dcfm.eastmoney.com/EM_MutiSvcExpandInterface/api/js/get?type=HSGT20_GGTJ_SUM&token=%s&st=LTZB&sr=-1&p=%s&ps=50&js=var qhgasAsJ={pages:(tp),data:(x)}&filter=(DateType='1' and HdDate='2020-07-03')&rt=53131790";

    /** 沪深港通每日持股统计（近1个月）*/
    private final static String  URL_HSGTHDSTA = "http://dcfm.eastmoney.com//em_mutisvcexpandinterface/api/js/get?type=HSGTHDSTA&token=%s&filter=(SCODE='%s')&st=HDDATE&sr=-1&p=1&ps=50&js=var yKYRKGdC={pages:(tp),data:(x)}&rt=53130646";


    public SpiderEastmoneyUtil() {
    }


    /**
     * 沪深港通每日持股统计（近1个月）
     * @param token
     * @param stockCode
     * @return
     * @throws IOException
     */
    public List<HsgthdstaBean> getHSGTHDSTAJsonData(String token, String stockCode) throws IOException {

        String html = pickData(String.format(URL_HSGTHDSTA,token,stockCode));

        String json = html.substring(html.indexOf("data:")+5,html.length()-1);

        log.info(json);
        return Arrays.asList(new Gson().fromJson(json, HsgthdstaBean[].class));

    }

    public List<HSGT20GGTJSumBean> getHSGT20_GGTJ_SUMJsonData(String token) throws IOException {

        List<HSGT20GGTJSumBean> lstData = new ArrayList<>();
        for(int i=0;i<20;i++){
            String html = pickData(String.format(URL_HSGT20_GGTJ_SUM,token,i+1));
            String json = html.substring(html.indexOf("data:")+5,html.length()-1);
            log.info(json);
            HSGT20GGTJSumBean[] hsgt20GGTJSumBeans = new Gson().fromJson(json, HSGT20GGTJSumBean[].class);
            List<HSGT20GGTJSumBean> hsgt20GGTJSumBeans1 = Arrays.asList(hsgt20GGTJSumBeans);
            lstData.addAll(hsgt20GGTJSumBeans1);
        }

        return lstData;

    }

    /*
     * 爬取网页信息
     */
    private String pickData(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
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
        return body.string();
    }

    public static void main(String[] args) throws IOException {
        long start=System.currentTimeMillis();
        String token = AlmanacUtil2.getToken();
        SpiderEastmoneyUtil util = new SpiderEastmoneyUtil();
        List<HSGT20GGTJSumBean> lstStocks = util.getHSGT20_GGTJ_SUMJsonData(token);
        assert lstStocks.size()==1000;
        lstStocks.stream().forEach( item -> {
            String sCode = item.getSCode();
            try {
                List<HsgthdstaBean> lstData = util.getHSGTHDSTAJsonData(token, sCode);
                log.info(lstData.toString());
            } catch (IOException e) {
                log.error("getHSGTHDSTAJsonData",e);
            }
        });
        log.info("spend time:[{}]s",(System.currentTimeMillis()-start)/1000);
    }

}
