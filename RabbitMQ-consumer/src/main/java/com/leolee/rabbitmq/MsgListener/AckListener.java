package com.leolee.rabbitmq.MsgListener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @ClassName AckListener
 * @Description: Consumer Ack
 * 1.设置手动确认签收:acknowledge-mode: manual, retry.enabled: true #是否支持重试
 * 2.实现ChannelAwareMessageListener接口，ChannelAwareMessageListener是MessageListener的子接口
 * 3.如果消息接收并处理完成，调用channel.basicAck()向MQ确认签收
 * 4.如果消息接收但是业务处理失败，调用channel.basicNack()拒收，要求MQ重新发送
 * @Author LeoLee
 * @Date 2020/11/7
 * @Version V1.0
 **/
@Component
public class AckListener implements ChannelAwareMessageListener {


    @RabbitListener(queues = "boot_queue")
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {

        Thread.sleep(1000);
        boolean tag = new String(message.getBody()).contains("true");
        System.out.println("接收到msg:" + new String(message.getBody()));
        //获取mes deliveryTag
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            if (tag) {
                System.out.println("业务处理成功");
                //手动签收
                /*
                 * deliveryTag：the tag from the received {@link com.rabbitmq.client.AMQP.Basic.GetOk} or {@link com.rabbitmq.client.AMQP.Basic.Deliver}
                 * multiple: ture确认本条消息以及之前没有确认的消息，false仅确认本条消息
                 */
                channel.basicAck(deliveryTag, false);
            } else {
                //模拟业务处理失败抛出异常
                System.out.println("业务处理失败");
                throw new IOException("业务处理失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            /*
             * deliveryTag：the tag from the received {@link com.rabbitmq.client.AMQP.Basic.GetOk} or {@link com.rabbitmq.client.AMQP.Basic.Deliver}
             * multiple: ture确认本条消息以及之前没有确认的消息，false仅确认本条消息
             * requeue: true该条消息重新返回MQ queue，MQ broker将会重新发送该条消息
             */
            channel.basicNack(deliveryTag, false, true);
            //也可以使用channel.basicReject(deliveryTag, requeue),它只能拒收单条消息
            //channel.basicReject(deliveryTag, true);
        }

    }
}
