package xyz.geekweb.stock;

/**
 * @author lhao
 */
public interface FinanceData {


    /**
     * 输出内容
     *
     * @return str
     */
    String toPrintout();

    /**
     * 是否通知（有监测值）
     * @return
     */
    boolean isNotify();
}
