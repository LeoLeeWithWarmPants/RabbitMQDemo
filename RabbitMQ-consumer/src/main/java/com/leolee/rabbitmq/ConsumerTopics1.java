package com.leolee.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName ConsumerPubSub1
 * @Description: Topics模式
 * @Author LeoLee
 * @Date 2020/11/6
 * @Version V1.0
 **/
public class ConsumerTopics1 {

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

        //5.创建队列queue（由于简单模式不需要Exchange，其实是使用默认的交换机，所以消息直接入队列），消费者可以不需要创建队列，但是创建了也不影响
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
        //生产者已经生命过了队列，可以忽略该步骤
        //channel.queueDeclare("workQueues", false, false, false, null);
        String queueName1 = "test_topic_queue1";

        //6.接收消息
        /*
         * String queue:队列名称
         * boolean autoAck:是否自动确认，当消费者收到消息之后会自动给MQ一个回执，告诉MQ消息已经收到
         * Consumer callback:回调方法
         */
        Consumer consumer = new DefaultConsumer(channel){

            /*
             * 功能描述: <br>
             * 〈回调方法〉当客户端收到消息并向MQ确认消息已经收到，将回调该方法
             * @Param: [consumerTag消息唯一标识,
             *  envelope获取一些信息，包含交换机信息、routing key...等,
             *  properties配置信息，生产者发送消息时候的配置,
             *  body数据]
             * @Return: void
             * @Author: LeoLee
             * @Date: 2020/11/5 11:56
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                /*System.out.println("consumerTag:" + consumerTag);
                System.out.println("envelope.exchange:" + envelope.getExchange());
                System.out.println("envelope.routingKey:" + envelope.getRoutingKey());
                System.out.println("properties:" + properties);*/
                System.out.println("消费者1从队列" + queueName1 + "接收到body:" + new String(body));

            }
        };
        channel.basicConsume(queueName1, true, consumer);

        //7.消费者不需要关闭资源，不然无法完成自动确认
    }
}
