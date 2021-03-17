package xyz.geekweb.stock.service.impl;


import com.sun.media.jfxmedia.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.geekweb.config.DataProperties;
import xyz.geekweb.stock.enums.FinanceTypeEnum;
import xyz.geekweb.stock.pojo.KZZBean;
import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeDataPOJO;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class KZZmplTest {

    @Autowired
    private DataProperties dataProperties;

    @Autowired
    private SearchFinanceData searchFinanceData;

    @Autowired
    private KZZImpl kzz;

    @Test
    public void testPrint() throws Exception {

        List<RealTimeDataPOJO> realTimeDataPOJOS = searchFinanceData.getAllData().get(FinanceTypeEnum.STOCK);

        kzz.fetchKZZData(realTimeDataPOJOS);
    }

    @Test
    public void SellStockAndBuyKzz() throws Exception {

        List<RealTimeDataPOJO> realTimeDataPOJOS = searchFinanceData.getAllData().get(FinanceTypeEnum.STOCK);

        kzz.SellStockAndBuyKzz(realTimeDataPOJOS);
    }


    @Test
    public void doLoopSellStockAndBuyKzz() throws Exception {

        while(true){
            SellStockAndBuyKzz();
            Thread.sleep(10000);
        }

    }

    @Test
    public void doLoopSellStockAndBuyKzz2() throws Exception {

        while(true){
            List<RealTimeDataPOJO> realTimeDataPOJOS = searchFinanceData.getAllData().get(FinanceTypeEnum.STOCK);

            List<KZZBean> kzzBean = kzz.getKzzBean(realTimeDataPOJOS, new String[]{"sh113016:sh601127:15.70"});
            log.info(kzzBean.toString());
            Thread.sleep(10 * 1000);
        }

    }

}