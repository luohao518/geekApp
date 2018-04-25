package xyz.geekweb.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.annotations.Test;

@Component
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