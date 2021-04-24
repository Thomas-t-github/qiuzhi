package com.tyt.qiuzhi.asyncmq.consumer;

import com.alibaba.fastjson.JSON;
import com.tyt.qiuzhi.asyncmq.EventModel;
import com.tyt.qiuzhi.elasticsearch.dao.SearchDAO;
import com.tyt.qiuzhi.elasticsearch.entity.SearchEntity;
import com.tyt.qiuzhi.model.Question;
import com.tyt.qiuzhi.model.User;
import com.tyt.qiuzhi.service.QuestionService;
import com.tyt.qiuzhi.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = {"search.direct.queue"})
public class SearchConsumer {

    @Autowired
    SearchDAO searchDAO;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @RabbitHandler
    public void doHandler(String eventModelString){

        EventModel eventModel = JSON.parseObject(eventModelString, EventModel.class);

        SearchEntity entity = new SearchEntity();

        Question question = questionService.selectById(eventModel.getEntityId());
        User user = userService.selectById(eventModel.getEntityOwnerId());

        entity.setId(question.getId());
        entity.setNickName(user.getNickName());
        entity.setLabel(question.getLabel());
        entity.setCommentCount(question.getCommentCount());
        entity.setDescription(question.getDescription());
        entity.setHeadUrl(user.getHeadUrl());
        entity.setCreatedDate(question.getCreatedDate());
        entity.setReward(question.getReward());
        entity.setTitle(question.getTitle());
        entity.setUserId(question.getUserId());

        searchDAO.save(entity);

    }
}
