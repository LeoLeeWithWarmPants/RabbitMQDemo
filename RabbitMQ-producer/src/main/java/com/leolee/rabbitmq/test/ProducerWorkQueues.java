package com.leolee.rabbitmq.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName ProducerHelloWorld
 * @Description: MQ生产者 work queues模式
 * @Author LeoLee
 * @Date 2020/11/5
 * @Version V1.0
 **/
public class ProducerWorkQueues {

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

        //5.创建队列queue（由于简单模式不需要Exchange，其实是使用默认的交换机，所以消息直接入队列）
        /*
         String queue:队列名称
         boolean durable:是否持久化
         boolean exclusive: 有如下两个意义
            是否独占，只有一个消费者监听这个队列
            当connection关闭时，是否删除队列
         boolean autoDelete:是否自动删除，当没有消费者的时候自动删除
         Map<String, Object> arguments: 一些配置参数
         */
        //如果没有一个名字叫Hello_world的队列，则会创建，如果存在该队列，则复用
        channel.queueDeclare("workQueues", false, false, false, null);

        //6.发送消息
        /*
         String exchange:交换机名称，简单模式下交换机使用默认的 ""
         String routingKey:路由名称，简单模式下就是队列名称
         boolean mandatory:
         boolean immediate:
         BasicProperties props:配置信息
         byte[] body:发送的消息数据
         */
        for (int i = 1; i <= 10; i++) {
            String msg = "[" + i + "]:Hello RabbitMQ";
            channel.basicPublish("", "workQueues", null, msg.getBytes());
        }

        //7.释放资源
        channel.close();
        connection.close();
    }
}
