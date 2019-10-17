package xyz.geekweb.delay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * TODO 超时未支付订单处理消息队列
 */
public class OrderOverTimeQueue {

    private RabbitmqConfiguration rabConf;

    //队列名称
    //****==================订单延时队列=======================*****//
    //订单延时队列
    public final String DELAY_QUEUE_NAME = "delay-queue-orderOverTime";
    //订单延时消费队列
    public final String CONSUME_QUEUE_NAME = "consume-queue-orderOverTime";
    //订单延时队列死信交换的交换器名称
    public final String EXCHANGENAME = "exchange-orderOverTime";
    //订单延时队列死信的交换器路由key
    public final String ROUTINGKEY = "routingKey-orderOverTime";

    private Channel delayChannel;//延时队列连接通道

    private Channel consumerChannel;//消费队列连接通道

    public void init() throws Exception{
        //创建连接通道
        delayChannel=rabConf.getConnection().createChannel();
        consumerChannel=rabConf.getConnection().createChannel();

        //创建交换器
        consumerChannel.exchangeDeclare(EXCHANGENAME,"direct");

        /**创建处理延时消息的延时队列*/
        Map <String,Object> arg = new HashMap <String,Object>();
        //配置死信交换器
        arg.put("x-dead-letter-exchange",EXCHANGENAME); //交换器名称
        //死信交换路由key （交换器可以将死信交换到很多个其他的消费队列，可以用不同的路由key 来将死信路由到不同的消费队列去）
        arg.put("x-dead-letter-routing-key", ROUTINGKEY);
        delayChannel.queueDeclare(DELAY_QUEUE_NAME, rabConf.isQUEUE_SAVE(), false, false, arg);

        /**创建消费队列*/
        consumerChannel.queueDeclare(CONSUME_QUEUE_NAME, rabConf.isQUEUE_SAVE(), false, false, null);
        //参数1:绑定的队列名  参数2:绑定至哪个交换器  参数3:绑定路由key
        consumerChannel.queueBind(CONSUME_QUEUE_NAME, EXCHANGENAME,ROUTINGKEY);
        //最多接受条数 0为无限制，每次消费消息数(根据实际场景设置)，true=作用于整channel,false=作用于具体的消费者
        consumerChannel.basicQos(0,10, false);

        //创建消费队列的消费者
        Consumer consumer = new DefaultConsumer(consumerChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,AMQP.BasicProperties properties, byte[] body)
                    throws IOException  {
                String message = new String(body, "UTF-8");
                try {
                    //业务逻辑处理
                    ConsumeMessage(message);
                    //确认消息已经消费 	参数2（true=设置后续消息为自动确认消费  false=为手动确认）
                    consumerChannel.basicAck(envelope.getDeliveryTag(), false);
                }catch (Exception e) {
                }
            }
        };

        boolean flag=false;//是否手动确认消息  true 是  false否
        consumerChannel.basicConsume(CONSUME_QUEUE_NAME, flag, consumer);
    }

    /**
     * 方法描述: 发送延迟订单处理消息
     * @param msg 消息内容 （订单号或者json格式字符串）
     * @param overTime 消息存活时间
     * @throws Exception
     */
    public void sendMessage(String msg,Long overTime) throws Exception{
        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .expiration(overTime.toString()) //设置消息存活时间（毫秒）
                .build();
        delayChannel.basicPublish("",DELAY_QUEUE_NAME, properties, msg.getBytes("UTF-8"));
    }

    /**
     *
     * 方法描述:
     * 业务逻辑说明: TODO(总结性的归纳方法业务逻辑)
     * @param msg 消费消息（订单号，或特定格式json字符串）
     * @throws InterruptedException
     */
    public void ConsumeMessage(String msg) throws InterruptedException {
        Thread.sleep(50);//模拟业务逻辑处理
        System.out.println("处理到期消息时间=="+System.currentTimeMillis());
        System.err.println("删除订单 order-number  ==  "+msg);
    }


    public RabbitmqConfiguration getRabConf() {
        return rabConf;
    }


    public void setRabConf(RabbitmqConfiguration rabConf) {
        this.rabConf = rabConf;
    }

    public static void main(String[] args) throws Exception {
        OrderOverTimeQueue ooto=new OrderOverTimeQueue();
        RabbitmqConfiguration rf= new RabbitmqConfiguration();
        rf.init();
        ooto.setRabConf(rf);
        ooto.init();

        //模拟用户产生订单 消息生存时长为30秒
        ooto.sendMessage("20180907-order-number", 10000l);

        System.out.println("创建消息时间=="+System.currentTimeMillis());



    }


}
