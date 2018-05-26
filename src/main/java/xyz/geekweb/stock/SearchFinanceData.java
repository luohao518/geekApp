package xyz.geekweb.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.geekweb.stock.enums.FinanceTypeEnum;
import xyz.geekweb.stock.impl.*;
import xyz.geekweb.stock.mq.Sender;
import xyz.geekweb.stock.savesinastockdata.RealTimeData;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lhao
 */
@Service
public class SearchFinanceData {

    @Autowired
    private DataProperties dataProperties;

    @Autowired
    private FjFundImpl fjFund;

    @Autowired
    private StockImpl stock;

    @Autowired
    private HBFundImpl hbFund;

    @Autowired
    private GZNHGImpl gznhg;

    @Autowired
    private Sender sender;

    @Autowired
    private FXImpl fx;

    private Logger logger = LoggerFactory.getLogger(SearchFinanceData.class);
    private Map<FinanceTypeEnum, FinanceData> lstFinanceData;

    /**
     * watchALLFinanceData
     *
     * @return str
     */
    public void watchALLFinanceData() {
        logger.debug("execute watchALLFinanceData()");

        this.fillALLData();
        lstFinanceData.forEach((k, v) -> {
            v.printInfo();
            v.sendNotify(sender);
        });
    }

    public Map<FinanceTypeEnum, FinanceData> getAllData(){
        fillALLData();
        return this.lstFinanceData;
    }

    /**
     * get all data
     */
    private void fillALLData() {

        logger.debug("execute fillALLData()");

        final List<RealTimeDataPOJO> realTimeDataPOJOS = fetchSinaData();

        this.lstFinanceData = new HashMap<>(10);
        this.gznhg.fetchData(realTimeDataPOJOS);
        this.lstFinanceData.put(FinanceTypeEnum.GZNHG, gznhg);

        this.hbFund.fetchData(realTimeDataPOJOS);
        this.lstFinanceData.put(FinanceTypeEnum.HB_FUND, hbFund);

        this.stock.fetchData(realTimeDataPOJOS);
        this.lstFinanceData.put(FinanceTypeEnum.STOCK, stock);

        this.lstFinanceData.put(FinanceTypeEnum.FJ_FUND, fjFund);

        fx.fetchData(this.dataProperties.getFx().toArray(new String[0]));
        this.lstFinanceData.put(FinanceTypeEnum.FX, fx);
    }

    private List<RealTimeDataPOJO> fetchSinaData() {

        List lstALL = new ArrayList(30);
        lstALL.addAll(dataProperties.getReverse_bonds());
        lstALL.addAll(dataProperties.getMonetary_funds());
        lstALL.addAll(dataProperties.getStocks());

        logger.debug("codes[{}]", lstALL);
        return RealTimeData.getRealTimeDataObjects(lstALL);
    }

}
