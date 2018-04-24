package xyz.geekweb.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import xyz.geekweb.util.MailService;

import java.io.IOException;

/**
 * @author lhao
 */
@Controller
@RequestMapping("/stock")
public class StockController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SearchStocks sotck;

    @Autowired
    private MailService mailService;

    @RequestMapping(method = RequestMethod.GET)
    public String showList() throws IOException {
        logger.info("do Get()");

        String s = sotck.doALL();
        logger.warn(s);

        mailService.sendSimpleMail(s);
        return "stock";
    }
}
