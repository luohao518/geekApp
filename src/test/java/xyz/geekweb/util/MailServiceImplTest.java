package xyz.geekweb.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import javax.validation.constraints.AssertTrue;

//@ContextConfiguration(classes = MailServiceImpl.class)
public class MailServiceImplTest extends AbstractTestNGSpringContextTests {

    //@Autowired
    //private MailService mailService;

    @Test
    public void testSimpleMail() throws Exception {
       // mailService.sendSimpleMail(" hello this is simple mail");

    }
}