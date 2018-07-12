package xyz.geekweb.stock;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.geekweb.stock.service.impl.SearchFinanceData;
import xyz.geekweb.util.RedisUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchFinanceDataTest {

    @Autowired
    private SearchFinanceData searchFinanceData;


    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void testRedis() throws Exception {

       searchFinanceData.saveSinaJslToRedis();
        searchFinanceData.saveSinaJslToRedis();

       searchFinanceData.getAllDataFromRedis();

        searchFinanceData.clearRedisData();


    }

    @Test
    public void testPushAndIndex() throws Exception {

        redisUtil.lLeftPush("test1","a");
        redisUtil.lLeftPush("test1","b");
        redisUtil.lLeftPush("test1","c");
        Assert.assertEquals("c",redisUtil.lGetIndex("test1",0));


    }

}