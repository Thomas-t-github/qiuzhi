package com.tyt.qiuzhi.controller;


import com.tyt.qiuzhi.asyncmq.EventModel;
import com.tyt.qiuzhi.asyncmq.EventProducer;
import com.tyt.qiuzhi.asyncmq.EventType;
import com.tyt.qiuzhi.model.EntityType;
import com.tyt.qiuzhi.model.HostHolder;
import com.tyt.qiuzhi.service.CommentService;
import com.tyt.qiuzhi.service.FollowService;
import com.tyt.qiuzhi.service.QuestionService;
import com.tyt.qiuzhi.service.UserService;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/follow")
public class FollowController {

    @Autowired
    FollowService followService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/followUser"}, method = {RequestMethod.POST, RequestMethod.GET},produces={"application/json;charset=UTF-8"})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId){
        if (hostHolder.getUser() == null){
            return QiuzhiUtils.getJSONString(1,"您还未登录，请先登录！");
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        //异步
        eventProducer.fireEvent("feed",new EventModel(EventType.FOLLOW)
                .setActorId(hostHolder.getUser().getId()).setEntityType(EntityType.ENTITY_USER)
                .setEntityId(userId).setEntityOwnerId(userId));

        return QiuzhiUtils.getJSONString(ret ? 0 : 1,"加好友成功");
    }

    @RequestMapping(path = {"/unfollowUser"}, method = {RequestMethod.POST, RequestMethod.GET},produces={"application/json;charset=UTF-8"})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId){
        if (hostHolder.getUser() == null){
            return QiuzhiUtils.getJSONString(1,"您还未登录，请先登录！");
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        //异步

        return QiuzhiUtils.getJSONString(ret ? 0 : 1,"取消关注成功");
    }

}
