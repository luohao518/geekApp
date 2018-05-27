package xyz.geekweb.stock;

import xyz.geekweb.stock.mq.Sender;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.util.List;

/**
 * @author lhao
 */
public interface FinanceData<T> {


    /**
     * 输出内容
     *
     */
    void printInfo();

    /**
     * 是否通知（有监测值）
     * @return
     */
    void sendNotify(Sender sender);

    List<T>  getData();
}
