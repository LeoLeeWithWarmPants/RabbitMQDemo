package com.leolee.rabbitmq;

        import com.leolee.rabbitmq.config.RabbitMQConfig;
        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.springframework.amqp.core.Message;
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

        //发送消息(可以故意写错exchange，模拟消息无法到达任何一个exchange)
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "boot.test", "test msg send");
    }


    /*
     * 功能描述: <br>
     * 〈回退模式〉
     * 1.开启回退模式 publisher-returns: true #开启return回退模式
     * 2.设置returnCallBack
     * 3.设置exchange处理消息的模式：
     *     3.1.如果消息没有路由到Queue,丢弃消息，没有回调(默认设置)
     *     3.2.如果消息没有路由到Queue,返回给消息的发送方ReturnCallBack
     * @Param: []
     * @Return: void
     * @Author: LeoLee
     * @Date: 2020/11/7 16:31
     */
    @Test
    public void testReturnModel() {

        /**
         * @Param: [
         * message 消息对象
         * replyCode 返回code，失败码
         * replyText 错误信息
         * exchange 失败所处的交换机
         * routingKey 消息的routingKey
         * ]
         */
        rabbitTemplate.setReturnCallback((Message message, int replyCode, String replyText, String exchange, String routingKey) -> {
            System.out.println("returnCallBack executed");
            System.out.println("replyCode:" + replyCode);
            System.out.println("replyText:" + replyText);
            System.out.println("exchange:" + exchange);
            System.out.println("routingKey:" + routingKey);
        });

        //设置交换机处理失败消息的模式为true，当消息没有路由到Queue,返回给消息的发送方ReturnCallBack
        rabbitTemplate.setMandatory(true);

        //发送消息(故意写错routingKey制造exchange消息分发错误)
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "11111.boot.test", "test msg send");
    }


    /*
     * 功能描述: <br>
     * 〈测试Consumer Ack〉
     * @Param: []
     * @Return: void
     * @Author: LeoLee
     * @Date: 2020/11/7 21:12
     */
    @Test
    public void testAck() {

//        //消费者接收到该消息，解析到true，就模拟调用channel.basicAck确认签收消息
//        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "boot.test", "test msg send [true]");

        //消费者接收到该消息，解析到false，就模拟调用channel.basicNack，拒收消息，让MQ重发
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "boot.test", "test msg send [false]");
    }


    /*
     * 功能描述: <br>
     * 〈测试客户端限流，多发几条给客户端〉
     * @Param: []
     * @Return: void
     * @Author: LeoLee
     * @Date: 2020/11/8 3:06
     */
    @Test
    public void testQos() {

        for (int i = 1; i <= 10; i++) {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "boot.test", "test msg send:[" + i + "]");
        }
    }


    /*
     * 功能描述: <br>
     * 〈测试过期时间〉
     * 1.队列的过期时间设置
     * 2.针对某条消息的过期时间设置
     *     如果设置了消息的过期时间，也设置了队列的过期时间，最终过期时间以两者中时间较短的为准
     *     消息过期后并不会直接被移除掉，MQ并不是轮询所有队列中消息的过期时间，只有消息在顶端的时候才会去判断该条消息是否过期
     * @Param: []
     * @Return: void
     * @Author: LeoLee
     * @Date: 2020/11/8 11:58
     */
    @Test
    public void testTTL() {

        for (int i = 0; i < 10; i++) {
            if ((i&1) == 1) {
                System.out.println("奇数：" + i);
                //该条消息测试队列过期
                rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "ttl.test", "test msg ttl");
            } else {
                System.out.println("偶数：" + i);
                //该条消息测试消息过期
                rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "ttl.test", "test msg ttl", message -> {
                    message.getMessageProperties().setExpiration("5000");
                    return message;
                });
            }
        }
    }


    /*
     * 功能描述: <br>
     * 〈测试死信队列〉
     * 1、过期的消息：队列中的消息到达了过期时间还没有被消费，就会变成死信
     * 2、队列中消息的长度到达限制：超过某个队列的最大存储消息个数之后的，其他被exchange分发到该队列的消息直接称为死信队列
     * 3、消费端拒接消息：当消费者端手动确认模式下拒绝了某条消息，并且设置了requeue=false之后，这条消息并不会被放回原队列，而是变为死信
     * @Param:
     * @Return:
     * @Author: LeoLee
     * @Date: 2020/11/8 14:50
     */
    @Test
    public void testDLX() {

        //这些消息将会被发到正常队列中
        for (int i = 0; i < 2; i++) {
            //该条消息测试队列过期
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "ttl.abc", "test msg dlx");

        }
    }

}
