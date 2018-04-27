package xyz.geekweb.stock;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class DataPropertiesTest {
    @Test
    public void testGetMap() throws Exception {
        System.out.println(new DataProperties().getMap());
    }

    @Test
    public void testGetList() throws Exception {
        System.out.println(new DataProperties().getList());
    }

    @Test
    public void testGetPrison() throws Exception {
    }

}