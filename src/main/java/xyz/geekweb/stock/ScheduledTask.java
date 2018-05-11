package xyz.geekweb.stock;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xyz.geekweb.util.HolidayUtil;
import xyz.geekweb.util.MailService;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lhao
 */
@Service
public class ScheduledTask {

    private SearchFinanceData searchFinanceData;
    private MailService mailService;
    private Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    public ScheduledTask(SearchFinanceData searchFinanceData, MailService mailService) {
        this.searchFinanceData = searchFinanceData;
        this.mailService = mailService;
    }
//    @Scheduled(fixedRate = 5000)
//    public void reportCurrentTime() throws InterruptedException {
//        System.out.println(String.format("---第%s次执行，当前时间为：%s", count0++, dateFormat.format(new Date())));
//    }
//
//    @Scheduled(fixedDelay = 5000)
//    public void reportCurrentTimeAfterSleep() throws InterruptedException {
//        System.out.println(String.format("===第%s次执行，当前时间为：%s", count1++, dateFormat.format(new Date())));
//    }

    @Scheduled(cron = "${geekweb.cron.exp}") //表示周一到周五每天上午9：15执行作业
    public void reportCurrentTimeCron() throws InterruptedException, IOException {

        logger.info("reportCurrentTimeCron() start");

        if (HolidayUtil.isHoliday() || HolidayUtil.isStockTimeEnd()) {
            logger.info("节假日或者已收盘，结束！");
            return;
        }

        ScheduledExecutorService scheduledThreadPool = new ScheduledThreadPoolExecutor(
                5,
                new BasicThreadFactory.Builder().namingPattern("scheduled-pool-%d").daemon(true).build());

        scheduledThreadPool.scheduleAtFixedRate(() -> {

            logger.info("执行轮询");

            if (HolidayUtil.isStockTimeEnd()) {
                logger.info("已收盘，今天执行程序退出！");
                scheduledThreadPool.shutdown();
            }
            if (HolidayUtil.isStockTime()) {
                searchFinanceData.watchALLFinanceData();

            }else{
                logger.info("休市时间！");
            }

        }, 0, 60, TimeUnit.SECONDS);

        scheduledThreadPool.scheduleAtFixedRate(() -> {
            if (HolidayUtil.isStockTimeEnd()) {
                logger.info("已收盘，今天执行程序退出！");
                scheduledThreadPool.shutdown();
            }
            if (HolidayUtil.isStockTime()) {
                //logger.info("发送邮件，30分钟间隔");
                //mailService.sendSimpleMail(searchFinanceData.watchALLFinanceData());
            }else{
                logger.info("休市时间！");
            }
        }, 0, 1800, TimeUnit.SECONDS);
    }
}