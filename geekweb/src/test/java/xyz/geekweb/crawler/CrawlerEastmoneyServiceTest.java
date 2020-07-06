package xyz.geekweb.crawler;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.geekweb.crawler.bean.HSGTSumBean;
import xyz.geekweb.crawler.bean.HSGTHdStaBean;
import xyz.geekweb.crawler.service.CrawlerEastmoneyService;

import java.io.IOException;
import java.util.List;


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
}