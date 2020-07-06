package xyz.geekweb.crawler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xyz.geekweb.crawler.bean.HSGTHdStaBean;
import xyz.geekweb.crawler.bean.HSGTSumBean;
import xyz.geekweb.crawler.dao.HSGTHdStaRepository;
import xyz.geekweb.crawler.dao.HSGTSumRepository;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author lhao
 */
@Service
@EnableScheduling
public class CrawlerScheduledTask {

    private CrawlerEastmoneyService service;

    @Autowired
    private HSGTSumRepository hsgtSumRepository;

    @Autowired
    private HSGTHdStaRepository hsgtHdStaRepository;


    @Autowired
    public CrawlerScheduledTask(CrawlerEastmoneyService service) {
        this.service = service;
    }


    @Scheduled(cron = "${geekweb.cron.crawler.exp}") //表示周一到周五每天上午9：15执行作业
    public void saveHsgtSumToMysql() throws IOException {
        String token = service.getToken();
        List<HSGTSumBean> hsgtSumBeans = service.getHSGTSumJsonData(token);
        Date now = new Date();
        for(HSGTSumBean bean : hsgtSumBeans){
            bean.setCreateDate(now);
            bean.setUpdateDate(now);
        }
        hsgtSumRepository.saveAll(hsgtSumBeans);
    }

    /**
     * 北向资金流入股票近30日数据存入mysql
     * @throws IOException
     */
    public void saveHSGTHdStaToMysql() throws IOException {
        String token = service.getToken();
        List<HSGTSumBean> lst = hsgtSumRepository.findAll();
        Date now = new Date();
        for(HSGTSumBean bean : lst){
            List<HSGTHdStaBean> hsgtHdStaJsonData = service.getHSGTHdStaJsonData(token, bean.getSCode());
            for(HSGTHdStaBean hsgtHdStaBean : hsgtHdStaJsonData){
                hsgtHdStaBean.setCreateDate(now);
                hsgtHdStaBean.setUpdateDate(now);
            }
            hsgtHdStaRepository.saveAll(hsgtHdStaJsonData);
        }
    }


}