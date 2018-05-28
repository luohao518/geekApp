package xyz.geekweb.stock.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import xyz.geekweb.config.DataProperties;
import xyz.geekweb.stock.enums.FinanceTypeEnum;
import xyz.geekweb.stock.mq.Sender;
import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeData;
import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeDataPOJO;
import xyz.geekweb.util.HolidayUtil;
import xyz.geekweb.util.RedisUtil;

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
    private FXImpl fx;

    @Autowired
    private RedisUtil redisUtil;

    private Logger logger = LoggerFactory.getLogger(SearchFinanceData.class);

    private static Map<FinanceTypeEnum, List<RealTimeDataPOJO>> lstFinanceData;


    public Map<FinanceTypeEnum, List<RealTimeDataPOJO>> getAllData(){

        if (HolidayUtil.isStockTime()) {
            fillALLData();
        }else{
            if(this.lstFinanceData==null){
                fillALLData();
            }
        }

        return this.lstFinanceData;
    }


    public void saveAllToRedis(){
        logger.debug("put data into redis");
        fillALLData();
        try {
            boolean result = redisUtil.lSet("lstFinanceData", this.lstFinanceData);
            Assert.isTrue(result,"lset");
        }catch (Exception exp){
            logger.error("redis put:",exp);
            throw  exp;
        }
    }

    public Map<FinanceTypeEnum, List<RealTimeDataPOJO>> getAllDataFromRedis(){

        Map<FinanceTypeEnum, List<RealTimeDataPOJO>> lstFinanceData = (Map<FinanceTypeEnum, List<RealTimeDataPOJO>>)redisUtil.lGetIndex("lstFinanceData",0);
        if (lstFinanceData != null){
            return lstFinanceData;
        }else{
            logger.warn("redis read data is null!");
            return getAllData();
        }

    }
    /**
     * get all data
     */
    private void fillALLData() {

        logger.debug("execute fillALLData()");

        final List<RealTimeDataPOJO> realTimeDataPOJOS = fetchSinaData();

        this.lstFinanceData = new HashMap<>(10);
        this.gznhg.fetchData(realTimeDataPOJOS);
        this.lstFinanceData.put(FinanceTypeEnum.GZNHG, gznhg.getData());

        this.hbFund.fetchData(realTimeDataPOJOS);
        this.lstFinanceData.put(FinanceTypeEnum.HB_FUND, hbFund.getData());

        this.stock.fetchData(realTimeDataPOJOS);
        this.lstFinanceData.put(FinanceTypeEnum.STOCK, stock.getData());

        this.fjFund.fetchData();
        this.lstFinanceData.put(FinanceTypeEnum.FJ_FUND, fjFund.getData());

        fx.fetchData(this.dataProperties.getFx().toArray(new String[0]));
        this.lstFinanceData.put(FinanceTypeEnum.FX, fx.getData());
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
