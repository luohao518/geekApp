package xyz.geekweb.stock.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import xyz.geekweb.util.MailService;

@Component
public class Receiver {

    private Logger logger = LoggerFactory.getLogger(Receiver.class);

    private MailService mailService;

    @Autowired
    public Receiver(MailService mailService){
        this.mailService=mailService;
    }

    @RabbitListener(queues = "mail",containerFactory="rabbitListenerContainerFactory")
    public void receiveMail(@Payload String msg) {
        logger.info("do receiveMail()");

        mailService.sendSimpleMail(msg);
    }


}