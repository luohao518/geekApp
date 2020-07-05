package xyz.geekweb.delay;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * TODO rabbitmq配置类
 */
public class RabbitmqConfiguration {
    private final String SERVER_HOST="192.168.1.98";//rabbitmq 服务器地址
    private final int PORT=5672;//端口号
    private final String USER_NAME="guest";//用户名
    private final String PASSWORD="guest";//密码
    private final boolean QUEUE_SAVE =true;//队列是否持久化
    private final String MESSAGE_SAVE = "1" ;//消息持久化  1，0
    //rabbitmq 连接工厂
    private final ConnectionFactory RAB_FACTORY = new ConnectionFactory();
    private Connection connection;

    public void init() throws Exception{
        RAB_FACTORY.setHost(SERVER_HOST);
        RAB_FACTORY.setPort(PORT);
        RAB_FACTORY.setUsername(USER_NAME);
        RAB_FACTORY.setPassword(PASSWORD);
        this.connection=RAB_FACTORY.newConnection();
    }
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isQUEUE_SAVE() {
        return QUEUE_SAVE;
    }

    public String getMESSAGE_SAVE() {
        return MESSAGE_SAVE;
    }

}
