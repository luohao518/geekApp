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

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private SearchFinanceData searchFinanceData;

    private MailService mailService;

    @Autowired
    public StockController(SearchFinanceData searchFinanceData, MailService mailService) {
        this.searchFinanceData = searchFinanceData;
        this.mailService = mailService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showList() throws IOException {
        logger.info("do Get()");

        String s = searchFinanceData.getALLDataForOutput();
        logger.warn(s);

        mailService.sendSimpleMail(s);
        return "stock";
    }
}
