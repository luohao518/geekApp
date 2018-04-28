package xyz.geekweb.stock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchFinanceDataTest {

    @Autowired
    private SearchFinanceData searchFinanceData;

    @Test
    public void testFetchALLData() throws Exception {
        searchFinanceData.fillALLData();
    }

    @Test
    public void testGetALLData() throws Exception {

        System.out.println(searchFinanceData.getALLDataForOutput());

    }

}