package xyz.geekweb.stock.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import xyz.geekweb.config.DataProperties;
import xyz.geekweb.stock.enums.FinanceTypeEnum;
import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeData;
import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeDataPOJO;
import xyz.geekweb.util.HolidayUtil;
import xyz.geekweb.util.RedisUtil;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

/**
 * @author lhao
 */
@Service
public class SearchFinanceData {

    public static final String LST_SINA_JSL_KEY = "LST_SINA_JSL_KEY";
    public static final String LST_FX_KEY = "LST_FX_KEY";
    private static Map<String, List<RealTimeDataPOJO>> lstFinanceDataIns;
    @Autowired
    private HttpSession session;
    @Autowired
    private DataProperties dataProperties;
    @Autowired
    private StockImpl stock;
    @Autowired
    private HBFundImpl hbFund;
    @Autowired
    private GZNHGImpl gznhg;
    @Autowired
    private FXImpl fx;
    @Autowired
    private KZZImpl kzz;
    @Autowired
    private RedisUtil redisUtil;
    private Logger logger = LoggerFactory.getLogger(SearchFinanceData.class);
    private Map<FinanceTypeEnum, List<RealTimeDataPOJO>> lstFinanceData;

    public void clearRedisData() {
        logger.debug("clear redis data ");
        try {
            LongStream.range(0, redisUtil.lGetListSize(LST_SINA_JSL_KEY)).forEach(item -> {
                redisUtil.lRightPop(LST_SINA_JSL_KEY);
            });

            LongStream.range(0, redisUtil.lGetListSize(LST_FX_KEY)).forEach(item -> {
                redisUtil.lRightPop(LST_FX_KEY);
            });

        } catch (Exception exp) {
            logger.error("clearRedisData:", exp);
            throw exp;
        }
    }

    public void saveSinaJslToRedis() {
        logger.debug("put data into redis");
        try {
            fillSinaJslData();
            boolean result = redisUtil.lLeftPush(LST_SINA_JSL_KEY, this.lstFinanceData);
            Assert.isTrue(result, "lset");
        } catch (Exception exp) {
            logger.error("redis put:", exp);
            throw exp;
        }
    }

    /**
     * get all data
     */
    private void fillSinaJslData() {

        logger.debug("execute fillSinaJslData()");

        //从sina获取数据
        final List<RealTimeDataPOJO> realTimeDataPOJOS = fetchSinaData();

        this.lstFinanceData = new HashMap<>(10);

        //填充国债逆回购数据
//        this.gznhg.fetchData(realTimeDataPOJOS);
//        this.lstFinanceData.put(FinanceTypeEnum.GZNHG, gznhg.getData());

        //填充货币基金数据
//        this.hbFund.fetchData(realTimeDataPOJOS);
//        this.lstFinanceData.put(FinanceTypeEnum.HB_FUND, hbFund.getData());

        //填充股票数据
        this.stock.fetchData(realTimeDataPOJOS);
        this.lstFinanceData.put(FinanceTypeEnum.STOCK, stock.getData());

        //填充分级数据
//        this.fjFund.fetchData();
//        this.lstFinanceData.put(FinanceTypeEnum.FJ_FUND, fjFund.getData());

        //填充可转债数据
        //this.kzz.fetchKZZData(realTimeDataPOJOS);
    }

   /* public void saveSinaJslToMem(){
        logger.debug("put data into memory");
        try {
            fillSinaJslData();
            lstFinanceDataIns=this.lstFinanceData;
        }catch (Exception exp){
            logger.error("saveSinaJslToMem:",exp);
            throw  exp;
        }
    }*/

    private List<RealTimeDataPOJO> fetchSinaData() {

        List lstALL = new ArrayList(30);
        lstALL.addAll(dataProperties.getReverse_bonds());
        lstALL.addAll(dataProperties.getMonetary_funds());
        lstALL.addAll(dataProperties.getStocks());

        logger.debug("codes[{}]", lstALL);
        return RealTimeData.getRealTimeDataObjects(lstALL);
    }

    public void saveFXToRedis() {
        logger.debug("put data into redis");
        try {
            fillFXData();
            boolean result = redisUtil.lLeftPush(LST_FX_KEY, this.lstFinanceData);
            Assert.isTrue(result, "lset");
        } catch (Exception exp) {
            logger.error("redis put:", exp);
            throw exp;
        }
    }

    /**
     * get all data
     */
    private void fillFXData() {

        logger.debug("execute fillFXData()");
        fx.fetchData(this.dataProperties.getFx().toArray(new String[0]));
        if (lstFinanceData == null) {
            this.lstFinanceData = new HashMap<>(10);
        }
        this.lstFinanceData.put(FinanceTypeEnum.FX, fx.getData());
    }

    public Map<FinanceTypeEnum, List<RealTimeDataPOJO>> getAllDataFromRedis() {

        Map<FinanceTypeEnum, List<RealTimeDataPOJO>> lstFinanceData1 =
                (Map<FinanceTypeEnum, List<RealTimeDataPOJO>>) redisUtil.lGetIndex(LST_SINA_JSL_KEY, 0);
        //Map<String, List<RealTimeDataPOJO>> lstFinanceData1 =this.lstFinanceDataIns;
        Map<String, List<RealTimeDataPOJO>> lstFinanceData2 =
                (Map<String, List<RealTimeDataPOJO>>) redisUtil.lGetIndex(LST_FX_KEY, 0);
        if (lstFinanceData1 != null) {
            if (lstFinanceData2 != null) {
                lstFinanceData1.put(FinanceTypeEnum.FX, lstFinanceData2.get("FX"));
            }
            return lstFinanceData1;
        } else {
            logger.warn("redis read data is null!");
            return getAllData();
        }

    }

    public Map<FinanceTypeEnum, List<RealTimeDataPOJO>> getAllData() {

        if (HolidayUtil.isStockTime()) {
            fillSinaJslData();
        } else {
            if (this.lstFinanceData == null) {
                fillSinaJslData();
            }
        }

        return this.lstFinanceData;
    }

}
