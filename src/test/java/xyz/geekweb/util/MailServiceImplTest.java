package xyz.geekweb.util;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailServiceImplTest {

    @Autowired
    private MailService mailService;

    @Test
    public void testSimpleMail() throws Exception {
        //mailService.sendSimpleMail("ityouknow@126.com","test simple mail"," hello this is simple mail");
        new MailServiceImpl().sendSimpleMail("ityouknow@126.com","test simple mail"," hello this is simple mail");
    }

}