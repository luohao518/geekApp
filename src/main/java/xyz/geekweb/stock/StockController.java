package xyz.geekweb.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import xyz.geekweb.stock.mq.Sender;
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

    private Sender sender;

    @Autowired
    public StockController(SearchFinanceData searchFinanceData,Sender sender) {
        this.searchFinanceData = searchFinanceData;
        this.sender=sender;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showList() throws IOException {
        logger.info("do Get()");

        String s = searchFinanceData.watchALLFinanceData();
        logger.warn(s);

        sender.sendMail(s);

        return "stock";
    }
}
