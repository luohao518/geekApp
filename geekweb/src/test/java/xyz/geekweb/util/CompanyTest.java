package xyz.geekweb.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author luohao
 * @date 2019/3/13
 */
public class CompanyTest {



    public static String trimSpace(String str){
        return str.replaceAll("\\s+", " ").trim();
    }

    @Test
    public void trimSpace1(){
        Assert.assertEquals("a b c",trimSpace("a b  c "));
        Assert.assertEquals("a b c",trimSpace("a b  c"));
        Assert.assertEquals("a b c",trimSpace(" a b c "));
    }

    public static int[] minValue(int[][] arrs){

        int minLength = arrs[0].length;
        for(int i=0;i<arrs.length;i++) {
            if(arrs[i].length<minLength){
                minLength = arrs[i].length;
            }
        }

        int[] result = new int[minLength];
        for(int i=0;i<arrs[0].length;i++) {
            Integer min=null;
            for (int j = 0; j < arrs.length; j++) {
                if (arrs[j].length > i) {
                    if(min==null){
                        min = arrs[j][i];
                    }
                    if (arrs[j][i] < min) {
                        min = arrs[j][i];
                    }
                }
            }
            if(i<result.length){
                result[i] = min;
            }
        }

        return result;
    }

    @Test
    public void minValue(){
        int[] actual = minValue(new int[][]{{1, 6, 8}, {5, 4}});
        Assert.assertEquals(Arrays.toString(new int[]{1,4}), Arrays.toString(actual));

        actual = minValue(new int[][]{{1, 2, 8}, {5, 5, 9}});
        Assert.assertEquals(Arrays.toString(new int[]{1,2,8}), Arrays.toString(actual));


    }
}
