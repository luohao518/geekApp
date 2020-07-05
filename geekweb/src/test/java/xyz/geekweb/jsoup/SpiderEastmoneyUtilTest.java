package xyz.geekweb.jsoup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpiderEastmoneyUtilTest {

    @Autowired
    private SpiderEastmoneyUtil service;

    @Test
    public void getHSGT20_GGTJ_SUMJsonData() throws IOException {
        String token = AlmanacUtil2.getToken();
        service.getHSGT20_GGTJ_SUMJsonData(token);
    }
}