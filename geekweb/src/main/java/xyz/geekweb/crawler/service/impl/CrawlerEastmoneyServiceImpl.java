package xyz.geekweb.crawler.service.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import xyz.geekweb.crawler.bean.HSGTSumBean;
import xyz.geekweb.crawler.bean.HSGTHdStaBean;
import xyz.geekweb.crawler.service.CrawlerEastmoneyService;
import xyz.geekweb.util.UrlUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class CrawlerEastmoneyServiceImpl implements CrawlerEastmoneyService {

    /** token*/
    private final static String URL_TOKEN="http://data.eastmoney.com/hsgtcg/StockHdStatistics.aspx?stock=600185";

    /** 北向资金流入股票-流通股占比排序*/
    private final static String URL_HSGT20_GGTJ_SUM="http://dcfm.eastmoney.com/EM_MutiSvcExpandInterface/api/js/get?type=HSGT20_GGTJ_SUM&token=%s&st=LTZB&sr=-1&p=%s&ps=50&js=var qhgasAsJ={pages:(tp),data:(x)}&filter=(DateType='1' and HdDate='%s')&rt=53131790";

    /** 沪深港通每日持股统计（近1个月）*/
    private final static String  URL_HSGTHDSTA = "http://dcfm.eastmoney.com//em_mutisvcexpandinterface/api/js/get?type=HSGTHDSTA&token=%s&filter=(SCODE='%s')&st=HDDATE&sr=-1&p=1&ps=50&js=var yKYRKGdC={pages:(tp),data:(x)}&rt=53130646";


    @Override
    public String getToken() throws IOException {
        String html = UrlUtil.getInstance().callUrlByGetReturnString(URL_TOKEN);
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

        throw new IllegalStateException("can't find token");
    }
    /**
     * 沪深港通每日持股统计（近1个月）
     * @param token
     * @param stockCode
     * @return
     * @throws IOException
     */
    @Override
    public List<HSGTHdStaBean> getHSGTHdStaJsonData(String token, String stockCode) throws IOException {

        String html = UrlUtil.getInstance().callUrlByGetReturnString(String.format(URL_HSGTHDSTA,token,stockCode));
        String json = html.substring(html.indexOf("data:")+5,html.length()-1);
        log.info(json);
        return Arrays.asList(new Gson().fromJson(json, HSGTHdStaBean[].class));

    }

    /**
     * 北向资金流入股票-流通股占比排序
     * @param token
     * @return
     * @throws IOException
     */
    @Override
    public List<HSGTSumBean> getHSGTSumJsonData(String token) throws IOException {

        List<HSGTSumBean> lstData = new ArrayList<>(1000);
        String yyyyMMdd = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //TODO
        yyyyMMdd = "2020-07-03";
        Gson gson=new Gson();
        for(int i=0;i<20;i++){
            String html = UrlUtil.getInstance().callUrlByGetReturnString(String.format(URL_HSGT20_GGTJ_SUM, token, i + 1,yyyyMMdd));
            String json = html.substring(html.indexOf("data:")+5,html.length()-1);
            log.info(json);
            HSGTSumBean[] hsgt20GGTJSumBeans = gson.fromJson(json, HSGTSumBean[].class);
            List<HSGTSumBean> hsgt20GGTJSumBeans1 = Arrays.asList(hsgt20GGTJSumBeans);
            lstData.addAll(hsgt20GGTJSumBeans1);
        }
        return lstData;

    }

}
