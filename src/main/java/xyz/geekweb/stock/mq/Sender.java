package xyz.geekweb.stock.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Sender {

    private Logger logger = LoggerFactory.getLogger(Sender.class);

    @Autowired
    private AmqpTemplate rabbitTemplate;

//    public void send() {
//        logger.info("do send()");
//
//        String context = "hello " + new Date();
//        System.out.println("Sender : " + context);
//        this.rabbitTemplate.convertAndSend("mail", context);
//    }

    public void sendMail(String msg) {
        logger.info("do sendMail()");

        this.rabbitTemplate.convertAndSend("mail", msg);
    }

}