package xyz.geekweb.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import xyz.geekweb.stock.enums.FinanceTypeEnum;
import xyz.geekweb.stock.mq.Sender;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;
import xyz.geekweb.util.MailService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author lhao
 */
@Controller
@RequestMapping("/stock")
public class StockController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private SearchFinanceData searchFinanceData;


    @Autowired
    public StockController(SearchFinanceData searchFinanceData) {
        this.searchFinanceData = searchFinanceData;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showList(Model model) throws IOException {
        logger.info("do Get()");

        //searchFinanceData.watchALLFinanceData();

        Map<FinanceTypeEnum, FinanceData> allData = searchFinanceData.getAllData();
        allData.forEach((k, v) -> {
            switch (k){
                case STOCK:
                    List<RealTimeDataPOJO> realTimeDataPOJO1= v.getData();
                    model.addAttribute(FinanceTypeEnum.STOCK.toString(),realTimeDataPOJO1);
                    break;
                case GZNHG:
                    List<RealTimeDataPOJO> realTimeDataPOJO2= v.getData();
                    model.addAttribute(FinanceTypeEnum.GZNHG.toString(),realTimeDataPOJO2);
                    break;
                case HB_FUND:
                    List<RealTimeDataPOJO> realTimeDataPOJO3= v.getData();
                    model.addAttribute(FinanceTypeEnum.HB_FUND.toString(),realTimeDataPOJO3);
                    break;
            }
        });

        return "showInfo";
    }
}
