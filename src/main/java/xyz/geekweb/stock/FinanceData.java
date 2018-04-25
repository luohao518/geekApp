package xyz.geekweb.stock;

import org.apache.poi.ss.formula.functions.T;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.util.List;

/**
 * @author lhao
 */
public interface FinanceData<E> {


    /**
     * 过滤数据
     * @param data data
     */
    void initData(List<E> data);

    /**
     * 输出内容
     * @return str
     */
    String print();
}
