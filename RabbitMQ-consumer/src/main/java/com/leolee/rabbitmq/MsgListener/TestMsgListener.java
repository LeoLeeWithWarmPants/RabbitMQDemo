package com.leolee.rabbitmq.MsgListener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName TestMsgListener
 * @Description: 监听MQ消息
 * @Author LeoLee
 * @Date 2020/11/7
 * @Version V1.0
 **/
@Component
public class TestMsgListener {

    public static final String EXCHANGE_NAME = "boot_topic_exchange";

    public static final String QUEUE_NAME = "boot_queue";

    @RabbitListener(queues = "boot_queue")
    public void listenerQueue(Message message) {

        System.out.println(message);
    }
}
