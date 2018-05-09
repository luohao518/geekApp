package xyz.geekweb.stock.mq;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import xyz.geekweb.stock.enums.FinanceTypeEnum;
import xyz.geekweb.stock.pojo.DataPO;
import xyz.geekweb.util.MailService;

import java.util.List;

import static java.util.Comparator.comparing;
import static xyz.geekweb.stock.mq.RabbitConfig.QUEUE_MAIL;
import static xyz.geekweb.stock.mq.RabbitConfig.QUEUE_NOTIFY;

/**
 * @author lhao
 */
@Component
public class Receiver {

    private Logger logger = LoggerFactory.getLogger(Receiver.class);

    private MailService mailService;

    @Autowired
    public Receiver(MailService mailService){
        this.mailService=mailService;
    }

    @RabbitListener(queues = QUEUE_MAIL,containerFactory="rabbitListenerContainerFactory")
    public void receiveMail(@Payload String msg) {
        logger.info("call receiveMail()");

        mailService.sendSimpleMail(msg);
    }

    @RabbitListener(queues = QUEUE_NOTIFY,containerFactory="rabbitListenerContainerFactory")
    public void receiveNotify(@Payload List<DataPO> lstDataPO) {
        logger.info("call receiveNotify()");

        StringBuilder sb=new StringBuilder();

        lstDataPO.stream().forEach( i ->
        {
            //分级
            if(i.getType()== FinanceTypeEnum.FJ_FUND){
                sb.append(String.format("%n分级A可以做轮动---%4s: 当前价[%5.3f] 净值[%5.3f] 净价[%5.3f] [%6s %6s]",
                        i.getBuyOrSaleEnum(), i.getNow(),i.getValue(),i.getTrueValue(), i.getFullCode(),i.getName()));
            }
        });
        logger.info(sb.toString());
        if(StringUtils.isNotEmpty(sb.toString())) {
            mailService.sendSimpleMail(sb.toString());
        }
    }

}