package com.tyt.qiuzhi.controller;

import com.tyt.qiuzhi.asyncmq.EventModel;
import com.tyt.qiuzhi.asyncmq.EventProducer;
import com.tyt.qiuzhi.asyncmq.EventType;
import com.tyt.qiuzhi.model.Comment;
import com.tyt.qiuzhi.model.EntityType;
import com.tyt.qiuzhi.model.HostHolder;
import com.tyt.qiuzhi.model.Question;
import com.tyt.qiuzhi.service.CommentService;
import com.tyt.qiuzhi.service.QuestionService;
import com.tyt.qiuzhi.service.UserService;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/jie")
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    QuestionService questionService;

    @Autowired
    EventProducer eventProducer;


    @RequestMapping(path = {"/reply"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map addComment(@RequestParam("qid") int questionId,
                             @RequestParam("content") String content){

        Map<String, Object> result = new HashMap<>();

        Question question = questionService.selectById(questionId);

        if (question == null){
            result.put("status",3);
            result.put("msg","帖子不存在！");
            return result;
        }
        try {
            if (StringUtils.isBlank(content)){
                result.put("status",1);
                result.put("msg","评论内容为空！");
                return result;
            }
            Comment comment = new Comment();
            comment.setContent(content);
            if (hostHolder.getUser() != null){
                comment.setUserId(hostHolder.getUser().getId());
            }else {
                result.put("status",2);
                result.put("msg","用户未登录，请先登录再评论！");
                return result;
            }
            comment.setCreatedDate(new Date());
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setStatus(0);
            comment.setLikeCount(0);
            commentService.addComment(comment);
            int commentCount = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(),commentCount);

            eventProducer.fireEvent("feed",new EventModel(EventType.COMMENT)
                    .setActorId(hostHolder.getUser().getId()).setEntityType(EntityType.ENTITY_USER)
                    .setEntityId(questionId).setEntityOwnerId(question.getUserId())
                    .setExt("commentContent",content));

        } catch (Exception e) {
            logger.error("添加评论失败："+e.getMessage());
        }
        result.put("status",0);
        result.put("qid",questionId);
        result.put("msg","评论成功");
        return result;
    }

}
