package com.leolee.rabbitmq.MsgListener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName QosListener
 * @Description: 消费者限流 QOS:服务质量（Quality of Service）
 * 1.确保ack机制为手动确认:acknowledge-mode: manual
 * 2.设置消费限流配置:prefetch: 2 代表消费端每次从MQ拉去两条消息处理，知道手动的确认完毕后，才继续从MQ拉去消息
 * @Author LeoLee
 * @Date 2020/11/8
 * @Version V1.0
 **/
@Component
public class QosListener implements ChannelAwareMessageListener {

    @RabbitListener(queues = "boot_queue")
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

        System.out.println("-----------------------------------------");
        System.out.println("msg:" + new String(message.getBody()));


        System.out.println("处理业务逻辑用时需要3秒中,当前时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
        Thread.sleep(3000);

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }
}
