package xyz.geekweb.stock;

import org.testng.annotations.Test;

public class SearchStocksTest {
    @Test
    public void testQueryGCX() throws Exception {

        new SearchStocks().doALL();
    }

}