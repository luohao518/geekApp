package xyz.geekweb.stock;



import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static xyz.geekweb.util.HolidayUtil.isHoliday;
import static xyz.geekweb.util.HolidayUtil.isStockTime;

public class HolidayUtilTest {

    @Test
    public void testIsStockTime() throws Exception {


        assertEquals(isStockTime("0914"), false);
        assertEquals(isStockTime("0939"), true);
        assertEquals(isStockTime("1131"), false);
        assertEquals(isStockTime("1200"), false);
        assertEquals(isStockTime("1501"), false);
        assertEquals(isStockTime("1401"), true);

    }

    @Test
    public void testIsHoliday() throws Exception {

        assertEquals(isHoliday("20180501"), true);
        assertEquals(isHoliday("20180420"), false);
        assertEquals(isHoliday("20180401"), true);
    }


}