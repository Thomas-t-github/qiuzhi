package com.tyt.qiuzhi.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DirectRabbitMQConfiguration {


    //1、声明注册direct模式的交换机
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange("direct_qiuzhi_exchange",true,false);
    }

    //2、声明队列
    @Bean
    public Queue messageQueue(){
        return new Queue("message.direct.queue",true);
    }

    @Bean
    public Queue feedQueue(){
        return new Queue("feed.direct.queue",true);
    }

    @Bean
    public Queue emailQueue(){
        return new Queue("email.direct.queue",true);
    }

    @Bean
    public Queue searchQueue(){
        Map<String,Object> args = new HashMap<>();
        //设置队列过期时间
        args.put("x-message-ttl",15000);
        //设置死信队列的交换机
        args.put("x-dead-letter-exchange","dead_direct_exchange");
        //设置死信队列的路由Key
        args.put("x-dead-letter-routing-key","dead");

        return new Queue("search.direct.queue",true,false,false,args);
    }

    //3、完成队列和交换机的绑定关系
    @Bean
    public Binding messageBinding(){
        return BindingBuilder.bind(messageQueue()).to(directExchange()).with("message");
    }

    @Bean
    public Binding feedBinding(){
        return BindingBuilder.bind(feedQueue()).to(directExchange()).with("feed");
    }

    @Bean
    public Binding emailBinding(){
        return BindingBuilder.bind(emailQueue()).to(directExchange()).with("email");
    }

    @Bean
    public Binding searchBinding(){
        return BindingBuilder.bind(searchQueue()).to(directExchange()).with("search");
    }

}
