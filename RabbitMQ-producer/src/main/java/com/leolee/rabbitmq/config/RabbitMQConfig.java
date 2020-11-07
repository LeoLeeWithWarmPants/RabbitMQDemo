package com.leolee.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName RabbitMQConfig
 * @Description: TODO
 * @Author LeoLee
 * @Date 2020/11/7
 * @Version V1.0
 **/
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "boot_topic_exchange";

    public static final String QUEUE_NAME = "boot_queue";

    //交换机
    @Bean("bootExchange")
    public Exchange bootExchange() {

        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }


    //队列
    @Bean("bootQueue")
    public Queue bootQueue() {

        return QueueBuilder.durable(QUEUE_NAME).build();
    }


    //交换机队列绑定关系
    @Bean
    public Binding bindQueueExchange(@Qualifier("bootQueue") Queue queue, @Qualifier("bootExchange") Exchange exchange) {

        return BindingBuilder.bind(queue).to(exchange).with("boot.#").noargs();//noargs,不传递参数
    }
}