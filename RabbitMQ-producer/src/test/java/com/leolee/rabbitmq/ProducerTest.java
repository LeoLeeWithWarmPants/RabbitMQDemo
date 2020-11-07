package com.leolee.rabbitmq;

import com.leolee.rabbitmq.config.RabbitMQConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName com.leolee.rabbitmq.ProducerTest
 * @Description: TODO
 * @Author LeoLee
 * @Date 2020/11/7
 * @Version V1.0
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProducerTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSend() {

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "boot.test", "test msg send");
    }

    /*
     * 功能描述: <br>
     * 〈确认模式〉
     *  1.开启确认模式publisher-confirms: true
     *  2.在RabbitTemplate定义ConfirmCallBack回调函数
     * @Param: []
     * @Return: void
     * @Author: LeoLee
     * @Date: 2020/11/7 15:58
     */
    @Test
    public void testConfirmModel() {

        //定义callback
        /*
         * correlationData 发送消息的时候的相关配置
         * ack exchange是否成功收到消息，true成功
         * cause 失败原因
         */
        rabbitTemplate.setConfirmCallback((@Nullable CorrelationData correlationData, boolean ack, @Nullable String cause) -> {
            System.out.println("confirmCallBack executed");

            if (ack) {
                System.out.println("exchange 接收消息成功");
            } else {
                System.out.println("exchange 接收消息失败");
                System.out.println("失败原因:" + cause);
            }
        });

        //发送消息
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "boot.test", "test msg send");
    }

}
