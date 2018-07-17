package xyz.geekweb.stock;


import java.util.Random;

public class DoNetTest {
    //@Test
    public void doPost() throws Exception {
        Random random = new Random();//创建随机对象
        int arrIdx = random.nextInt(100);//随机数组索引，nextInt(len-1)表示随机整数[0,(len-1)]之间的值
        for (int i = 0; i < arrIdx; i++) {

            DoNet.doPost(String.valueOf(i));
        }
    }


}
