package xyz.geekweb.stock;

import static org.testng.Assert.assertEquals;
import static xyz.geekweb.stock.HolidayUtil.*;

public class HolidayUtilTest {
    @org.testng.annotations.Test
    public void testIsStockTime() throws Exception {

        assertEquals(isStockTime("0914"), false);
        assertEquals(isStockTime("0939"), true);
        assertEquals(isStockTime("1131"), false);
        assertEquals(isStockTime("1200"), false);
        assertEquals(isStockTime("1501"), false);
        assertEquals(isStockTime("1401"), true);
//        assertEquals(isStockTimeEnd(), true);


    }

    @org.testng.annotations.Test
    public void testIsHoliday() throws Exception {

        assertEquals(isHoliday("20180501"), true);
        assertEquals(isHoliday("20180420"), false);
        assertEquals(isHoliday("20180401"), true);
    }


}