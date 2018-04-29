package xyz.geekweb.stock.impl;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.geekweb.stock.DataProperties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FXImplTest {

    @Autowired
    private DataProperties dataProperties;

    @Test
    public void testPrint() throws Exception {

        System.out.println(new FXImpl(dataProperties.getFx().toArray(new String[0])).print());
    }

}