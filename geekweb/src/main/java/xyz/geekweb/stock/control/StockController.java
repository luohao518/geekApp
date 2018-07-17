package xyz.geekweb.stock.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import xyz.geekweb.stock.enums.FinanceTypeEnum;
import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeDataPOJO;
import xyz.geekweb.stock.service.impl.SearchFinanceData;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    public String showList(String refresh, Model model) throws IOException {
        logger.info("do getAllData()");

        model.addAttribute("refresh", refresh);
        model.addAttribute("datetime", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        Map<String, List<RealTimeDataPOJO>> allData = searchFinanceData.getAllDataFromRedis();
        allData.forEach((k, v) -> {
            switch (k) {
                case "STOCK":
                    model.addAttribute(FinanceTypeEnum.STOCK.toString(), v);
                    break;
                case "GZNHG":
                    model.addAttribute(FinanceTypeEnum.GZNHG.toString(), v);
                    break;
                case "HB_FUND":
                    model.addAttribute(FinanceTypeEnum.HB_FUND.toString(), v);
                    break;
                case "FJ_FUND":
                    model.addAttribute(FinanceTypeEnum.FJ_FUND.toString(), v);
                    break;
                case "FX":
                    model.addAttribute(FinanceTypeEnum.FX.toString(), v);
                    break;
                default:
                    logger.warn("参数错误！");
                    break;
            }
        });

        return "showInfo";
    }
}
