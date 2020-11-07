package com.leolee.rabbitmq;

import com.leolee.rabbitmq.config.RabbitMQConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
}
