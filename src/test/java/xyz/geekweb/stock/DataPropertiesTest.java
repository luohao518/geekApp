package xyz.geekweb.stock;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DataPropertiesTest  {

    @Autowired
    private DataProperties dataProperties;

    @Test
    public void testMap(){
        Assert.assertNotNull(dataProperties);
        Assert.assertNotNull(dataProperties.getMap());
    }


    @Test
    public void testGetList() throws Exception {
        Assert.assertNotNull(dataProperties);
        Assert.assertNotNull(dataProperties.getFj_funds());
        Assert.assertNotNull(dataProperties.getReverse_bonds());
        Assert.assertNotNull(dataProperties.getFj_funds_have());
        Assert.assertNotNull(dataProperties.getMonetary_funds());
        Assert.assertNotNull(dataProperties.getStocks());
        Assert.assertNotNull(dataProperties.getStocks_others());
    }

}