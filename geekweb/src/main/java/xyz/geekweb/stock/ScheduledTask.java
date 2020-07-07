//package xyz.geekweb.stock;
//
//import org.apache.commons.lang3.concurrent.BasicThreadFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import xyz.geekweb.stock.service.impl.SearchFinanceData;
//import xyz.geekweb.util.HolidayUtil;
//import xyz.geekweb.util.MailService;
//
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author lhao
// */
//@Service
//@EnableScheduling
//public class ScheduledTask {
//
//    private SearchFinanceData searchFinanceData;
//    private Logger logger = LoggerFactory.getLogger(ScheduledTask.class);
//
//    @Autowired
//    public ScheduledTask(SearchFinanceData searchFinanceData, MailService mailService) {
//        this.searchFinanceData = searchFinanceData;
//    }
//
////    @Scheduled(fixedRate = 120000)
////    public void reportCurrentTime() {
////        if (!HolidayUtil.isHoliday()) {
////            logger.debug("执行FX任务");
////            searchFinanceData.saveFXToRedis();
////        }
////    }
//
//    @Scheduled(cron = "${geekweb.cron.exp}") //表示周一到周五每天上午9：15执行作业
//    public void reportCurrentTimeCron() {
//
//        logger.debug("reportCurrentTimeCron() start");
//
//        ScheduledExecutorService scheduledThreadPool = new ScheduledThreadPoolExecutor(
//                5,
//                new BasicThreadFactory.Builder().namingPattern("scheduled-pool-%d").daemon(true).build());
//
//        //每5秒查询数据
//        scheduledThreadPool.scheduleAtFixedRate(() -> {
//
//            if (HolidayUtil.isStockTime()) {
//                logger.debug("执行SinaJsl任务");
//                searchFinanceData.saveSinaJslToRedis();
//                //searchFinanceData.saveSinaJslToMem();
//            }
//        }, 0, 5, TimeUnit.SECONDS);
//
//        //清理数据
//        scheduledThreadPool.scheduleAtFixedRate(() -> {
//            searchFinanceData.clearRedisData();
//        }, 0, 3, TimeUnit.HOURS);
//    }
//}