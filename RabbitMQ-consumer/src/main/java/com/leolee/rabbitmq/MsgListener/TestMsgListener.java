package com.leolee.rabbitmq.MsgListener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName TestMsgListener
 * @Description: 监听MQ消息(测试需要,暂时屏蔽@RabbitListener(queues = "boot_queue"))
 * @Author LeoLee
 * @Date 2020/11/7
 * @Version V1.0
 **/
@Component
//@RabbitListener(queues = "boot_queue")
public class TestMsgListener {

    public static final String QUEUE_NAME = "boot_queue";

//    @RabbitListener(queues = "boot_queue")
    public void listenerQueue(Message message) {

        System.out.println("方法上@RabbitListener:" + new String(message.getBody()));
    }


    /*
     * 功能描述: <br>
     * 〈类上@RabbitListener + 方法上@RabbitHandler = 方法上@RabbitListener〉
     * @Param: [str, channel, message]
     * @Return: void
     * @Author: LeoLee
     * @Date: 2020/11/7 20:23
     */
    @RabbitHandler
    public void listenerQueue(String str, Channel channel, Message message) {

        System.out.println("类上@RabbitListener + 方法上@RabbitHandler:");
        System.out.println("str:" + str);
        System.out.println("msg:" + new String(message.getBody()));
    }
}
