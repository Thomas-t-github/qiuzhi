package com.tyt.qiuzhi.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.tyt.qiuzhi.async.EventHandler;
import com.tyt.qiuzhi.async.EventModel;
import com.tyt.qiuzhi.async.EventType;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class FeedHandler implements EventHandler {

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void doHandle(EventModel eventModel) {
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

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.COMMENT,EventType.FOLLOW});
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
