package xyz.geekweb.util;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

//@ContextConfiguration(classes = MailServiceImpl.class)
public class MailServiceImplTest extends AbstractTestNGSpringContextTests {

    //@Autowired
    //private MailService mailService;

    @Test
    public void testSimpleMail() throws Exception {
        // mailService.sendSimpleMail(" hello this is simple mail");

    }
}