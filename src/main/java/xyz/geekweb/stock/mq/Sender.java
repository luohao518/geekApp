package xyz.geekweb.stock.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.geekweb.stock.savesinastockdata.RealTimeDataPOJO;

import java.util.List;

import static xyz.geekweb.stock.mq.RabbitConfig.QUEUE_MAIL;
import static xyz.geekweb.stock.mq.RabbitConfig.QUEUE_NOTIFY;

/**
 * @author lhao
 */
@Component
public class Sender {

    private Logger logger = LoggerFactory.getLogger(Sender.class);

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void sendMail(String msg) {
        logger.info("call sendMail()");

        this.rabbitTemplate.convertAndSend(QUEUE_MAIL, msg);
    }

    public void sendNotify(List<RealTimeDataPOJO> lstDataPO) {

        if(lstDataPO!=null && lstDataPO.size()>0) {

            logger.info("call sendNotify()");
            this.rabbitTemplate.convertAndSend(QUEUE_NOTIFY, lstDataPO);
        }
    }

}