package com.tyt.qiuzhi.async.handler;

import com.tyt.qiuzhi.async.EventHandler;
import com.tyt.qiuzhi.async.EventModel;
import com.tyt.qiuzhi.async.EventType;
import com.tyt.qiuzhi.model.Message;
import com.tyt.qiuzhi.model.Question;
import com.tyt.qiuzhi.model.User;
import com.tyt.qiuzhi.service.MessageService;
import com.tyt.qiuzhi.service.QuestionService;
import com.tyt.qiuzhi.service.UserService;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//@Component
public class ViolationHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel eventModel) {

        User user = userService.selectById(eventModel.getEntityOwnerId());

        Message message = new Message();
        message.setHasRead(0);
        message.setToId(eventModel.getEntityOwnerId());
        message.setFromId(QiuzhiUtils.SYSTEM_USERID);
        message.setCreatedDate(new Date());
        message.setConversationId(QiuzhiUtils.SYSTEM_USERID < eventModel.getEntityOwnerId() ? String.format("%d_%d", QiuzhiUtils.SYSTEM_USERID, eventModel.getEntityOwnerId()) : String.format("%d_%d", eventModel.getEntityOwnerId(), QiuzhiUtils.SYSTEM_USERID));
        message.setContent("用户"+user.getNickName()+",您于"+ eventModel.getExt("createdDate")
                +"发布的帖子：《" +eventModel.getExt("questionTitle")+"》 包含违规信息，现已被删除，请不要发布包含违规信息的帖子！！！");

        messageService.addMessage(message);

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.VIOLATION_QUESTION);
    }
}
