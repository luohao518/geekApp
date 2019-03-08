package xyz.geekweb.stock.service.impl;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.geekweb.config.DataProperties;
import xyz.geekweb.stock.enums.FinanceTypeEnum;
import xyz.geekweb.stock.pojo.savesinastockdata.RealTimeDataPOJO;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
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

}