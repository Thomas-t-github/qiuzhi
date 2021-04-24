package com.tyt.qiuzhi.asyncmq.consumer;

import com.alibaba.fastjson.JSON;
import com.tyt.qiuzhi.asyncmq.EventModel;
import com.tyt.qiuzhi.model.Message;
import com.tyt.qiuzhi.model.User;
import com.tyt.qiuzhi.service.MessageService;
import com.tyt.qiuzhi.service.UserService;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RabbitListener(queues = {"message.direct.queue"})
public class MessageConsumer {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @RabbitHandler
    public void doHandler(String eventModelString){
        EventModel eventModel = JSON.parseObject(eventModelString, EventModel.class);

        User user = userService.selectById(eventModel.getEntityOwnerId());

        Message message = new Message();
        message.setHasRead(0);
        message.setToId(eventModel.getEntityOwnerId());
        message.setFromId(eventModel.getActorId());
        message.setCreatedDate(new Date());
        message.setConversationId(eventModel.getActorId() < eventModel.getEntityOwnerId() ? String.format("%d_%d", eventModel.getActorId(), eventModel.getEntityOwnerId()) : String.format("%d_%d", eventModel.getEntityOwnerId(), eventModel.getActorId()));
        message.setContent(eventModel.getExt("content"));

        messageService.addMessage(message);
    }

}
