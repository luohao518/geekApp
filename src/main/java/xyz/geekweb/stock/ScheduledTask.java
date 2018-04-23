package xyz.geekweb.stock;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTask {


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
        //System.out.println(String.format("+++第%s次执行，当前时间为：%s", count2++, dateFormat.format(new Date())));

        if (HolidayUtil.isHoliday()) return;
        if (HolidayUtil.isStockTimeEnd()) return;

        Thread thread = new Thread(() -> {
            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Boolean result = false;
                int count = 0;
                while (!result) {
                    try {
                        count++;
                        System.out.println(sdf.format(new Date()) + "--循环执行第" + count + "次");
                        new SearchStocks().doALL();
                        Thread.sleep(60 * 1000); //设置暂停的时间 60 秒
//                        if (count == 3) {
//                            result = true;
//                            break ;
//                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();


    }

}