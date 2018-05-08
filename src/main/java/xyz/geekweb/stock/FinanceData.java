package xyz.geekweb.stock;

import xyz.geekweb.stock.mq.Sender;

/**
 * @author lhao
 */
public interface FinanceData {


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
}
