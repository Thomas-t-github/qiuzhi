package com.tyt.qiuzhi.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeadRabbitMQConfiguration {

    //1、声明注册direct模式交换机
    @Bean
    public DirectExchange deadDirectExchange(){
        return new DirectExchange("dead_direct_exchange",true,false);
    }

    //2、声明队列
    @Bean
    public Queue deadQueue(){
        return new Queue("dead.direct.queue",true);
    }

    //3、把消息队列绑定到交换机和指定路由key
    @Bean
    public Binding deadBinding(){
        return BindingBuilder.bind(deadQueue()).to(deadDirectExchange()).with("dead");
    }



}
