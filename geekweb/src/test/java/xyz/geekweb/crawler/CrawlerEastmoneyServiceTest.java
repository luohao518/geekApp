package xyz.geekweb.crawler;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.geekweb.crawler.bean.HSGTSumBean;
import xyz.geekweb.crawler.bean.HSGTHdStaBean;
import xyz.geekweb.crawler.bean.kzz.CbNewBean;
import xyz.geekweb.crawler.service.CrawlerEastmoneyService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CrawlerEastmoneyServiceTest {

    @Autowired
    private CrawlerEastmoneyService service;

    @Test
    public void test() throws IOException {
        long start=System.currentTimeMillis();
        String token = service.getToken();
        List<HSGTSumBean> lstStocks = service.getHSGTSumJsonData(token);
        assert lstStocks.size()==1000;
        lstStocks.stream().forEach( item -> {
            String sCode = item.getSCode();
            try {
                List<HSGTHdStaBean> lstData = service.getHSGTHdStaJsonData(token, sCode);
                log.info(lstData.toString());
            } catch (IOException e) {
                log.error("getHSGTHDSTAJsonData",e);
            }
        });
        log.info("spend time:[{}]s",(System.currentTimeMillis()-start)/1000);
    }

    @Test
    public void searchStock() throws Exception {

        //可转债一览表
        List<CbNewBean> cbNewJsonData = service.getCbNewJsonData();
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

        List<HSGTSumBean> hsgtSumBeanList = service.searchStocks();
        for(HSGTSumBean item:hsgtSumBeanList){
            List<HSGTHdStaBean> hsgtHdStaBeans = service.searchStock(item.getSCode());
            long size=hsgtHdStaBeans.size();
            String sCode = item.getSCode();
            if(hmCbNew.get(sCode) ==null){
                //排除非可转债标的物
                continue;
            }

            for(int i=0;i<10;i++){
                HSGTHdStaBean hsgtHdStaBean = hsgtHdStaBeans.get(i);
                double sum1 = hsgtHdStaBean.getShareholdSum();
                double sum2 = hsgtHdStaBeans.get(i+1).getShareholdSum();
                double percent=((sum1-sum2)/sum2)*100;
                //市值
                double shareholdPrice=hsgtHdStaBean.getShareholdPrice()/100000000;

                if(percent>8.0f && shareholdPrice>1){
                    String strPercent = String.format("%.2f", percent);
                    String strShareholdPrice = String.format("%.2f", shareholdPrice);

                    log.info("[{}] {} {}% {} {}万股 {}亿", hsgtHdStaBean.getHdDate().substring(0,10), hsgtHdStaBean.getSName(),strPercent, hsgtHdStaBean.getZdf(),hsgtHdStaBean.getShareholdSum()/10000,strShareholdPrice);
                }

                if(percent<-8.0f && shareholdPrice>1){
                    String strPercent = String.format("%.2f", percent);
                    String strShareholdPrice = String.format("%.2f", shareholdPrice);

                    log.info("减持： [{}] {} {}% {} {}万股 {}亿", hsgtHdStaBean.getHdDate().substring(0,10), hsgtHdStaBean.getSName(),strPercent, hsgtHdStaBean.getZdf(),hsgtHdStaBean.getShareholdSum()/10000,strShareholdPrice);
                }
            }
        }
    }
}