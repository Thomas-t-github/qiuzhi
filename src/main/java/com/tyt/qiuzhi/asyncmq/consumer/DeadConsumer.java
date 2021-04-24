package com.tyt.qiuzhi.asyncmq.consumer;

import com.alibaba.fastjson.JSON;
import com.tyt.qiuzhi.asyncmq.EventModel;
import com.tyt.qiuzhi.controller.QuestionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RabbitListener(queues = {"dead.direct.queue"})
public class DeadConsumer {
    private static final Logger logger = LoggerFactory.getLogger(DeadConsumer.class);


    @RabbitHandler
    public void doHandler(String eventModelString){

        logger.error(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" : 消息消费失败，消息内容为："+eventModelString);

    }

}
