package xyz.geekweb.crawler.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import xyz.geekweb.crawler.bean.HSGTSumBean;
import xyz.geekweb.crawler.bean.HSGTHdStaBean;
import xyz.geekweb.crawler.bean.kzz.CbNewBean;
import xyz.geekweb.crawler.dao.HSGTHdStaRepository;
import xyz.geekweb.crawler.dao.HSGTSumRepository;
import xyz.geekweb.crawler.service.CrawlerEastmoneyService;
import xyz.geekweb.util.HolidayUtil;
import xyz.geekweb.util.UrlUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class CrawlerEastmoneyServiceImpl implements CrawlerEastmoneyService {

    /** token*/
    private final static String URL_TOKEN="http://data.eastmoney.com/hsgtcg/StockHdStatistics.aspx?stock=600185";

    /** 北向资金流入股票-流通股占比排序*/
    private final static String URL_HSGT20_GGTJ_SUM="http://dcfm.eastmoney.com/EM_MutiSvcExpandInterface/api/js/get?type=HSGT20_GGTJ_SUM&token=%s&st=LTZB&sr=-1&p=%s&ps=50&js=var qhgasAsJ={pages:(tp),data:(x)}&filter=(DateType='1' and HdDate='%s')&rt=53131790";

    /** 沪深港通每日持股统计（近1个月）*/
    private final static String  URL_HSGTHDSTA = "http://dcfm.eastmoney.com//em_mutisvcexpandinterface/api/js/get?type=HSGTHDSTA&token=%s&filter=(SCODE='%s')&st=HDDATE&sr=-1&p=1&ps=50&js=var yKYRKGdC={pages:(tp),data:(x)}&rt=53130646";

    /** 记事录可转债一览*/
    private final static String  URL_JSL_CBNEW = "https://www.jisilu.cn/data/cbnew/redeem_list/";


    @Autowired
    private HSGTSumRepository hsgtSumRepository;

    @Autowired
    private HSGTHdStaRepository hsgtHdStaRepository;

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
     * 从即使如接口获取可转债信息
     * @return
     * @throws IOException
     */
    @Override
    public List<CbNewBean> getCbNewJsonData() throws IOException {
        JSONObject jsonObject = UrlUtil.getInstance().callUrlByGet(String.format(URL_JSL_CBNEW));
        JSONArray rows = jsonObject.getJSONArray("rows");
        return Arrays.asList(new Gson().fromJson(rows.toJSONString(), CbNewBean[].class));
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

        String yyyyMMdd = getPreWorkingDay();
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

    /**
     * 取得上一个交易日
     * @return
     * @throws IOException
     */
    private String getPreWorkingDay() throws IOException {
        LocalDate baseDate = LocalDate.now();
        LocalDate preDate;
        while(true){
            preDate = baseDate.plusDays(-1);
            if(! HolidayUtil.isHoliday(preDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")))){
                break;
            }else{
                baseDate = preDate;
            }
        }
        return preDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public List<HSGTSumBean> searchStocks()  {
        HSGTSumBean bean = new HSGTSumBean();
        Example<HSGTSumBean> example = Example.of(bean);
        return hsgtSumRepository.findAll(example);
    }

    @Override
    public List<HSGTHdStaBean> searchStock(String scode)  {
        HSGTHdStaBean bean = new HSGTHdStaBean();
        bean.setSCode(scode);
        Example<HSGTHdStaBean> example = Example.of(bean);
        Sort sort = new Sort(Sort.Direction.DESC, "hdDate");
        return hsgtHdStaRepository.findAll(example,sort);
    }

    @Override
    public String analysisStocks() throws IOException {

        StringBuilder sb = new StringBuilder();
        //可转债一览表(集思录读取)
        List<CbNewBean> cbNewJsonData = this.getCbNewJsonData();
        Map<String, Integer> hmKzz = filterKZZ(cbNewJsonData);
        //搜索北向资金进入股票一览表
        List<HSGTSumBean> hsgtSumBeanList = this.searchStocks();
        for(HSGTSumBean stock : hsgtSumBeanList){
            List<HSGTHdStaBean> hsgtHdStaBeans = this.searchStock(stock.getSCode());
            long size=hsgtHdStaBeans.size();
            String sCode = stock.getSCode();
            if(hmKzz.get(sCode) ==null){
                //排除非可转债标的物
                continue;
            }

            for(int i=0;i<10;i++){
                //最近10个交易日数据
                if(hsgtHdStaBeans.size()==0){
                    log.error("can't find data:{}",stock.getSCode());
                }
                HSGTHdStaBean hsgtHdStaBean = hsgtHdStaBeans.get(i);
                double sum1 = hsgtHdStaBean.getShareholdSum();
                double sum2 = hsgtHdStaBeans.get(i+1).getShareholdSum();
                double percent=((sum1-sum2)/sum2)*100;
                //市值
                double shareholdPrice=hsgtHdStaBean.getShareholdPrice()/100000000;

                if(percent>8.0f && shareholdPrice>1){
                    String strPercent = String.format("%.2f", percent);
                    String strShareholdPrice = String.format("%.2f", shareholdPrice);

//                    log.info("[{}] {} {}% {} {}万股 {}亿", hsgtHdStaBean.getHdDate().substring(0,10), hsgtHdStaBean.getSName(),strPercent, hsgtHdStaBean.getZdf(),hsgtHdStaBean.getShareholdSum()/10000,strShareholdPrice);
                    String format = String.format("[%s] %s %s %s%% %s %s万股 %s亿", hsgtHdStaBean.getHdDate().substring(0, 10), hsgtHdStaBean.getSCode(),hsgtHdStaBean.getSName(), strPercent, hsgtHdStaBean.getZdf(), hsgtHdStaBean.getShareholdSum() / 10000, strShareholdPrice);
                    sb.append(format).append("\n");
                }

                if(percent<-8.0f && shareholdPrice>1){
                    String strPercent = String.format("%.2f", percent);
                    String strShareholdPrice = String.format("%.2f", shareholdPrice);

//                    log.info("减持： [{}] {} {}% {} {}万股 {}亿", hsgtHdStaBean.getHdDate().substring(0,10), hsgtHdStaBean.getSName(),strPercent, hsgtHdStaBean.getZdf(),hsgtHdStaBean.getShareholdSum()/10000,strShareholdPrice);
                    String format = String.format("减持：[%s] %s %s %s%% %s %s万股 %s亿", hsgtHdStaBean.getHdDate().substring(0, 10), hsgtHdStaBean.getSCode(),hsgtHdStaBean.getSName(), strPercent, hsgtHdStaBean.getZdf(), hsgtHdStaBean.getShareholdSum() / 10000, strShareholdPrice);
                    sb.append(format).append("\n");
                }
            }
        }
        return sb.toString();
    }

    private Map<String, Integer> filterKZZ(List<CbNewBean> cbNewJsonData) {
        Map<String, Integer> hmCbNew = new HashMap<>(cbNewJsonData.size());

        for(CbNewBean cbNewBean : cbNewJsonData){
            double calculatPrice = cbNewBean.getCell().calculatPrice();
            log.info("{}  {}",cbNewBean.getCell().getStock_nm(),calculatPrice);
            //转债价格
            double dPrice = Double.parseDouble(cbNewBean.getCell().getPrice());
            if(calculatPrice < 10 && dPrice>100 && dPrice<130) {
                //溢价率5个点以下,价格130以下
                hmCbNew.put(cbNewBean.getCell().getStock_id(), 1);
            }
        }
        return hmCbNew;
    }
}
