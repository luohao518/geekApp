package xyz.geekweb.crawler;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.geekweb.crawler.bean.HSGTHdStaBean;
import xyz.geekweb.crawler.service.CrawlerScheduledTask;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CrawlerScheduledTaskTest {

    @Autowired
    private CrawlerScheduledTask task;

    @Test
    public void all() throws Exception {
        task.saveHsgtSumToMysql();
        task.saveHSGTHdStaToMysql();
        task.analysisStocks();
    }

    /**
     * 北向资金流入股票（前1000只）存入mysql
     * @throws Exception
     */
    @Test
    public void saveHsgtSumToMysql() throws Exception {
        task.saveHsgtSumToMysql();
    }


    /**
     * 北向资金流入股票近30日数据存入mysql
     * @throws Exception
     */
    @Test
    public void saveHSGTHdStaToMysql() throws IOException {
        task.saveHSGTHdStaToMysql();
    }

    @Test
    public void analysisStocks() throws Exception {

        task.analysisStocks();
    }
}