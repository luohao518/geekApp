package xyz.geekweb.stock.service.impl;

import xyz.geekweb.stock.mq.Sender;

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
