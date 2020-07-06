package xyz.geekweb.crawler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.geekweb.crawler.service.CrawlerScheduledTask;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlerScheduledTaskTest {

    @Autowired
    private CrawlerScheduledTask task;

    @Test
    public void saveHsgtSumToMysql() throws Exception {
        task.saveHsgtSumToMysql();
    }


    @Test
    public void saveHSGTHdStaToMysql() throws Exception {
        task.saveHSGTHdStaToMysql();
    }
}