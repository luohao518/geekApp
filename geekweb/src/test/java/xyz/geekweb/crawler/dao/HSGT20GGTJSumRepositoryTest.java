package xyz.geekweb.crawler.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.geekweb.crawler.bean.HSGTSumBean;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HSGT20GGTJSumRepositoryTest{

    @Autowired
    private HSGTSumRepository repository;

    @Test
    public void test() throws Exception {

        HSGTSumBean bean = new HSGTSumBean();
        bean.setSCode("600185");
        bean.setSName("name1");
        Date now = new Date();
        bean.setCreateDate(now);
        bean.setUpdateDate(now);
        repository.save(bean);
        bean = new HSGTSumBean();
        bean.setSCode("601098");
        bean.setSName("name2");
        bean.setCreateDate(now);
        bean.setUpdateDate(now);
        repository.save(bean);

        Assert.assertEquals(2, repository.findAll().size());
        Assert.assertEquals("600185", repository.findBySCode("600185").getSCode());
        repository.delete(repository.findBySCode("600185"));
        repository.delete(repository.findBySCode("601098"));
    }


}