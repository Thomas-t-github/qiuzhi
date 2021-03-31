package com.tyt.qiuzhi.async.handler;

import com.tyt.qiuzhi.async.EventHandler;
import com.tyt.qiuzhi.async.EventModel;
import com.tyt.qiuzhi.async.EventType;
import com.tyt.qiuzhi.model.Message;
import com.tyt.qiuzhi.service.MessageService;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class QQLoginHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Override
    public void doHandle(EventModel eventModel) {

        Message message = new Message();
        message.setHasRead(0);
        message.setToId(eventModel.getActorId());
        message.setFromId(QiuzhiUtils.SYSTEM_USERID);
        message.setCreatedDate(new Date());
        message.setConversationId(QiuzhiUtils.SYSTEM_USERID < eventModel.getActorId() ? String.format("%d_%d", QiuzhiUtils.SYSTEM_USERID, eventModel.getActorId()) : String.format("%d_%d", eventModel.getActorId(), QiuzhiUtils.SYSTEM_USERID));
        message.setContent(eventModel.getExt("content"));

        messageService.addMessage(message);

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.QQ_LOGIN);
    }
}
