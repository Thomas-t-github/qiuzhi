package com.tyt.qiuzhi.controller;

import com.tyt.qiuzhi.asyncmq.EventModel;
import com.tyt.qiuzhi.asyncmq.EventProducer;
import com.tyt.qiuzhi.asyncmq.EventType;
import com.tyt.qiuzhi.model.Comment;
import com.tyt.qiuzhi.model.EntityType;
import com.tyt.qiuzhi.model.HostHolder;
import com.tyt.qiuzhi.service.CommentService;
import com.tyt.qiuzhi.service.LikeService;
import com.tyt.qiuzhi.util.JedisAdapter;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/api")
public class LikeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    CommentService commentService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("id") int commentId,
                    @RequestParam("ok") boolean isLike){

        if (hostHolder.getUser() == null){
            return QiuzhiUtils.getJSONString(1,"请先登录再点赞！");
        }

        if (isLike){
            likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        }else {
            likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        }

        jedisAdapter.sadd(RedisKeyUtil.getLikeCountSetKey(),String.valueOf(commentId));

        //发送异步事件
       /* Comment comment = commentService.selectById(commentId);
        eventProducer.fireEvent("feed",new EventModel(EventType.LIKE)
                .setActorId(hostHolder.getUser().getId()).setEntityId(commentId)
                .setEntityType(EntityType.ENTITY_COMMENT).setEntityOwnerId(comment.getUserId())
                .setExt("questionId",String.valueOf(comment.getEntityId())));*/


        return QiuzhiUtils.getJSONString(0,"点赞成功！");
    }

}
