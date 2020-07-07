package xyz.geekweb.crawler.service;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author lhao
 */
@Service
@EnableScheduling
public class CrawlerScheduledTask {

    private final CrawlerEastmoneyService service;

    @Autowired
    private HSGTSumRepository hsgtSumRepository;

    @Autowired
    private HSGTHdStaRepository hsgtHdStaRepository;


    @Autowired
    public CrawlerScheduledTask(CrawlerEastmoneyService service) {
        this.service = service;
    }


    @Scheduled(cron = "${geekweb.cron.crawler.exp}") //表示周一到周五每天上午9：15执行作业
    public void saveToMysql() throws IOException {

        this.saveHsgtSumToMysql();

        this.saveHSGTHdStaToMysql();

        this.analysisStocks();
    }

    /**
     * 北向资金流入股票一览表数据存入mysql
     * @throws IOException
     */
    public void saveHsgtSumToMysql() throws IOException {
        String token = service.getToken();
        List<HSGTSumBean> hsgtSumBeans = service.getHSGTSumJsonData(token);
        Date now = new Date();
        for(HSGTSumBean bean : hsgtSumBeans){
            HSGTSumBean hsgtSumBean=
                    hsgtSumRepository.findBySCode(bean.getSCode());
            if(hsgtSumBean ==null){
                //不存在时候才去插入
                bean.setCreateDate(now);
                bean.setUpdateDate(now);
                hsgtSumRepository.save(bean);
            }else{
                //如果存在则更新这条数据
                Date createDate = hsgtSumBean.getCreateDate();
                Long id = hsgtSumBean.getId();
                BeanUtils.copyProperties(bean,hsgtSumBean);
                hsgtSumBean.setCreateDate(createDate);
                hsgtSumBean.setUpdateDate(now);
                hsgtSumBean.setId(id);
                hsgtSumRepository.save(hsgtSumBean);
            }
        }
    }

    /**
     * 北向资金流入股票近30日数据存入mysql
     * @throws IOException
     */
    public void saveHSGTHdStaToMysql() throws IOException {
        String token = service.getToken();
        List<HSGTSumBean> lst = hsgtSumRepository.findAll();
        //List<HSGTSumBean> lst = Arrays.asList(hsgtSumRepository.findBySCode("002002"));
        Date now = new Date();
        for(HSGTSumBean bean : lst){
            List<HSGTHdStaBean> hsgtHdStaJsonData = service.getHSGTHdStaJsonData(token, bean.getSCode());
            for(HSGTHdStaBean hsgtHdStaBean : hsgtHdStaJsonData){

                HSGTHdStaBean hdStaBean=
                        hsgtHdStaRepository.findBySCodeAndHdDate(hsgtHdStaBean.getSCode(), hsgtHdStaBean.getHdDate());
                if(hdStaBean ==null){
                    //不存在时候才去插入
                    hsgtHdStaBean.setCreateDate(now);
                    hsgtHdStaBean.setUpdateDate(now);
                    hsgtHdStaRepository.save(hsgtHdStaBean);
                }
            }

        }
    }

    public void analysisStocks() throws IOException {

        String log = service.analysisStocks();
        String yyyyMmdd = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        FileUtils.writeStringToFile(
                new File("D:\\reports\\"+yyyyMmdd+".txt"),log);
    }
}