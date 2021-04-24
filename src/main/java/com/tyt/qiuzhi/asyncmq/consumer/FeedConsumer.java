package com.tyt.qiuzhi.asyncmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.tyt.qiuzhi.asyncmq.EventModel;
import com.tyt.qiuzhi.asyncmq.EventType;
import com.tyt.qiuzhi.model.EntityType;
import com.tyt.qiuzhi.model.Feed;
import com.tyt.qiuzhi.model.Question;
import com.tyt.qiuzhi.model.User;
import com.tyt.qiuzhi.service.FeedService;
import com.tyt.qiuzhi.service.FollowService;
import com.tyt.qiuzhi.service.QuestionService;
import com.tyt.qiuzhi.service.UserService;
import com.tyt.qiuzhi.util.JedisAdapter;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
@RabbitListener(queues = {"feed.direct.queue"})
public class FeedConsumer {

    @Autowired
    FeedService feedService;

    @Autowired
    UserService userService;

    @Autowired
    FollowService followService;

    @Autowired
    QuestionService questionService;

    @Autowired
    JedisAdapter jedisAdapter;


    @RabbitHandler
    public void doHandler(String eventModelString){

        EventModel eventModel = JSON.parseObject(eventModelString, EventModel.class);

        Feed feed = new Feed();
        feed.setType(eventModel.getType().getValue());
        feed.setUserId(eventModel.getActorId());
        feed.setCreatedDate(new Date());
        feed.setData(buildFeedData(eventModel));
        if (feed.getData() == null){
            return;
        }
        feedService.addFeed(feed);
        //推模式
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, eventModel.getActorId(), Integer.MAX_VALUE);
        followers.add(0);
        for (Integer follower : followers) {
            String timelineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdapter.lpush(timelineKey,String.valueOf(feed.getId()));
            //限制队列长度，如果超过，则删除队列后面的新鲜事
        }
    }

    private String buildFeedData(EventModel eventModel){
        HashMap<String, String> map = new HashMap<>();

        User user = userService.selectById(eventModel.getActorId());
        if (user == null){
            return null;
        }
        map.put("userId",String.valueOf(user.getId()));
        map.put("userHeadUrl",user.getHeadUrl());
        map.put("nickName",user.getNickName());

        if (eventModel.getType() == EventType.FOLLOW &&
                eventModel.getEntityType() == EntityType.ENTITY_USER){
            User entityUser = userService.selectById(eventModel.getEntityId());
            if (entityUser == null){
                return null;
            }
            map.put("entityId",String.valueOf(entityUser.getId()));
            map.put("entityName",entityUser.getNickName());
            map.put("entityHeadUrl",entityUser.getHeadUrl());

            return JSONObject.toJSONString(map);
        }
        if (eventModel.getType() == EventType.COMMENT){
            Question question = questionService.selectById(eventModel.getEntityId());
            if (question == null){
                return null;
            }
            map.put("entityId",String.valueOf(question.getId()));
            map.put("entityName",question.getTitle());
            map.put("entityContent",eventModel.getExt("commentContent"));

            return JSONObject.toJSONString(map);
        }


        return null;
    }

}
