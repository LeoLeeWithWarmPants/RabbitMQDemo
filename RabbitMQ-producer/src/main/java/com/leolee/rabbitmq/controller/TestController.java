package com.leolee.rabbitmq.controller;

import com.leolee.rabbitmq.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TestController
 * @Description: TODO
 * @Author LeoLee
 * @Date 2020/11/7
 * @Version V1.0
 **/
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(value = "/testSend", method = RequestMethod.GET)
    public void testSend() {

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "boot.test", "test msg send");
    }
}
