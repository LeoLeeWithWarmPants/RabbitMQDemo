package com.leolee.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

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

    public static final String DEAD_LETTER_EXCHANGE_NAME = "dlx_exchange";


    public static final String QUEUE_NAME = "boot_queue";

    public static final String QUEUE_TTL_NAME = "ttl_queue";

    public static final String DEAD_LETTER_QUEUE_NAME = "dlx_queue";

    //交换机
    @Bean("bootExchange")
    public Exchange bootExchange() {

        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }


    //死信交换机
    @Bean("deadLetterExchange")
    public Exchange deadLetterExchange() {

        return ExchangeBuilder.topicExchange(DEAD_LETTER_EXCHANGE_NAME).durable(true).build();
    }


    //队列
    @Bean("bootQueue")
    public Queue bootQueue() {

        return QueueBuilder.durable(QUEUE_NAME).build();
    }


    //设置TTL的队列
    @Bean("queueWithTTL")
    public Queue queueWithTTL() {

        Map<String, Object> argumentsMap = new HashMap<>();
        //设置队列超时时间
        argumentsMap.put("x-message-ttl", 50000);
        //设置该队列的长度
        argumentsMap.put("x-max-length", 10);
        //设置该队列的死信队列
        argumentsMap.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE_NAME);
        //设置死信的routing key,能保证在死信交换机中能匹配到死信队列即可
        argumentsMap.put("x-dead-letter-routing-key", "dlx.test");
        return QueueBuilder.durable(QUEUE_TTL_NAME).withArguments(argumentsMap).build();
    }


    //对应死信交换机的队列
    @Bean("deadLetterQueue")
    public Queue deadLetterQueue() {

        return QueueBuilder.durable(DEAD_LETTER_QUEUE_NAME).build();
    }


    //交换机队列绑定关系
    @Bean
    public Binding bindQueueExchange(@Qualifier("bootQueue") Queue queue, @Qualifier("bootExchange") Exchange exchange) {

        return BindingBuilder.bind(queue).to(exchange).with("boot.#").noargs();//noargs,不传递参数
    }


    //绑定TTL队列到交换机
    @Bean
    public Binding bingQueueTTLExchange(@Qualifier("queueWithTTL") Queue queue, @Qualifier("bootExchange") Exchange exchange) {

        return BindingBuilder.bind(queue).to(exchange).with("ttl.#").noargs();
    }


    //绑定死信队列到死信交换机
    @Bean
    public Binding bingDLQToDLX(@Qualifier("deadLetterQueue") Queue queue, @Qualifier("deadLetterExchange") Exchange exchange) {

        return BindingBuilder.bind(queue).to(exchange).with("dlx.#").noargs();
    }
}
