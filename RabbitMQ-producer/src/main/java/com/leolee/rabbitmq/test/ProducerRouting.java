package com.leolee.rabbitmq.test;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName ProducerPubSub
 * @Description: Rounting模式
 * @Author LeoLee
 * @Date 2020/11/6
 * @Version V1.0
 **/
public class ProducerRouting {

    public static void main(String[] args) throws IOException, TimeoutException {

        //1.创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();

        //2.设置参数
        connectionFactory.setHost("127.0.0.1");//默认值为localhost
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/LeoLee");//默认值为：/
        connectionFactory.setUsername("LeoLee");
        connectionFactory.setPassword("lyl512240816");

        //3.创建连接 connection
        Connection connection = connectionFactory.newConnection();

        //4.创建channel
        Channel channel = connection.createChannel();

        //5.创建交换机
        /*
        String exchange:交换机名称
        BuiltinExchangeType type:交换机类型，枚举，
        boolean durable:是否持久化
        boolean autoDelete:是否自动删除，当没有消费者的时候自动删除
        boolean internal:是否内部使用，一般都是false，true代表给MQ内部使用的，比如给MQ开发的插件使用
        Map<String, Object> arguments:相关参数
         */
        String exchangerName = "test_direct";
        channel.exchangeDeclare(exchangerName, BuiltinExchangeType.DIRECT, false, false, false, null);

        //6.创建队列
        /*
         String queue:队列名称
         boolean durable:是否持久化
         boolean exclusive: 有如下两个意义
            是否独占，只有一个消费者监听这个队列
            当connection关闭时，是否删除队列
         boolean autoDelete:是否自动删除，当没有消费者的时候自动删除
         Map<String, Object> arguments: 一些配置参数
         */
        //如果没有一个名字叫xxx的队列，则会创建，如果存在该队列，则复用
        String queueName1 = "test_direct_queue1";
        String queueName2 = "test_direct_queue2";
        channel.queueDeclare(queueName1, false, false, false, null);
        channel.queueDeclare(queueName2, false, false, false, null);

        //7.绑定队列和交换机
        /*
        String queue:队列名称
        String exchange:交换机名称
        String routingkey:路由键，绑定规则
            如果交换机的类型为 fanout，routingkey设置为空值""
        */
        channel.queueBind(queueName1, exchangerName, "errorLog");

        channel.queueBind(queueName2, exchangerName, "infoLog");
        channel.queueBind(queueName2, exchangerName, "warningLog");

        //8.发送消息
        String body1 = "日志信息[error]：又是不想当社畜的一天";
        String body2 = "日志信息[info]：又是不想当社畜的一天";
        String body3 = "日志信息[warning]：又是不想当社畜的一天";
        channel.basicPublish(exchangerName, "infoLog", null, body2.getBytes());//第二个参数routingkey为空
        channel.basicPublish(exchangerName, "errorLog", null, body1.getBytes());//第二个参数routingkey为空
        channel.basicPublish(exchangerName, "warningLog", null, body3.getBytes());//第二个参数routingkey为空

        //9.释放资源
        channel.close();
        connection.close();

    }

}
