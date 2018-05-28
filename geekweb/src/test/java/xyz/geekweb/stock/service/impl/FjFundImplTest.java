package xyz.geekweb.stock.service.impl;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FjFundImplTest {

    @Autowired
    private  FjFundImpl fjFund;

    @Test
    public void test1() throws Exception {
        fjFund.printInfo();

    }

}