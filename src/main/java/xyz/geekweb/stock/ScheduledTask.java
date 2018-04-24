package xyz.geekweb.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xyz.geekweb.util.MailService;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author lhao
 */
@Service
public class ScheduledTask {

    @Autowired
    private SearchStocks stocks;

    @Autowired
    private MailService mailService;

    private Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
//    @Scheduled(fixedRate = 5000)
//    public void reportCurrentTime() throws InterruptedException {
//        System.out.println(String.format("---第%s次执行，当前时间为：%s", count0++, dateFormat.format(new Date())));
//    }
//
//    @Scheduled(fixedDelay = 5000)
//    public void reportCurrentTimeAfterSleep() throws InterruptedException {
//        System.out.println(String.format("===第%s次执行，当前时间为：%s", count1++, dateFormat.format(new Date())));
//    }

    @Scheduled(cron = "0 15 9 ? * MON-FRI") //表示周一到周五每天上午9：15执行作业
    public void reportCurrentTimeCron() throws InterruptedException, IOException {

        if (HolidayUtil.isHoliday() || HolidayUtil.isStockTimeEnd()) {
            logger.info("节假日或者已收盘，结束！");
            return;
        }

        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
        scheduledThreadPool.scheduleAtFixedRate(() -> {

            logger.info("执行轮询");
            try {
                if (HolidayUtil.isStockTimeEnd()) {
                    logger.info("已收盘，今天执行程序退出！");
                    scheduledThreadPool.shutdown();
                }
                if (HolidayUtil.isStockTime()) {
                    String result = stocks.doALL();
                    logger.warn(result);
                }
            } catch (IOException e) {
                logger.error("run error", e);
            }
        }, 3, 60, TimeUnit.SECONDS);

        scheduledThreadPool.scheduleAtFixedRate(() -> {

            logger.info("发送邮件，30分钟间隔");
            try {
                if (HolidayUtil.isStockTimeEnd()) {
                    logger.info("已收盘，今天执行程序退出！");
                    scheduledThreadPool.shutdown();
                }
                if (HolidayUtil.isStockTime()) {
                    mailService.sendSimpleMail(stocks.doALL());
                }
            } catch (IOException e) {
                logger.error("run error", e);
            }
        }, 5, 1800, TimeUnit.SECONDS);
    }
}