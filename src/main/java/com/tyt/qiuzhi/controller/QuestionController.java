package com.tyt.qiuzhi.controller;

import com.tyt.qiuzhi.async.EventModel;
import com.tyt.qiuzhi.async.EventProducer;
import com.tyt.qiuzhi.async.EventType;
import com.tyt.qiuzhi.model.HostHolder;
import com.tyt.qiuzhi.model.Question;
import com.tyt.qiuzhi.model.User;
import com.tyt.qiuzhi.model.ViewObject;
import com.tyt.qiuzhi.service.QuestionService;
import com.tyt.qiuzhi.service.UserService;
import com.tyt.qiuzhi.util.LabelKeyUtil;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/jie")
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(value = "/detail/{qid}", method = {RequestMethod.GET})
    public String questionDetail(Model model, @PathVariable("qid") int qid){

        Question question = questionService.selectById(qid);
        User owner = userService.selectById(question.getUserId());
        model.addAttribute("question",question);
        model.addAttribute("owner",owner);
        /*
        List<Comment> comments = commentService.getCommentsByEntity(question.getId(), EntityType.ENTITY_QUESTION);
        ArrayList<ViewObject> vos = new ArrayList<>();
        for (Comment comment : comments) {
            ViewObject vo = new ViewObject();
            vo.set("comment",comment);
            vo.set("user",userService.selectById(comment.getUserId()));

            if (hostHolder.getUser() == null){
                vo.set("liked",0);
            }else {
                vo.set("liked",likeService.getLikeStatus(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,comment.getId()));
            }
            vo.set("likeCount",likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));

            vos.add(vo);
        }
        model.addAttribute("comments",vos);

        ArrayList<ViewObject> followUsers = new ArrayList<>();

        List<Integer> followerUids = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
        for (Integer followerUid : followerUids) {
            ViewObject vo = new ViewObject();
            User user = userService.selectById(followerUid);
            if (user == null){
                continue;
            }
            vo.set("name",user.getName());
            vo.set("headUrl",user.getHeadUrl());
            vo.set("id",user.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers",followUsers);

        if (hostHolder.getUser() != null){
            model.addAttribute("followed",followService.isFollower(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,qid));
        }else {
            model.addAttribute("followed",false);
        }*/

        return "jie/detail";
    }


    @RequestMapping("/addQuestion")
    @ResponseBody
    public String addQuestion(@RequestParam("label") String label,
                              @RequestParam("title") String title,
                              @RequestParam("university") String university,
                              @RequestParam("industry") String industry,
                              @RequestParam("description") String description,
                              @RequestParam("reward") String reward){

        try {
            Question question = new Question();
            question.setCommentCount(0);
            question.setCreatedDate(new Date());
            if (hostHolder.getUser() != null){
                question.setUserId(hostHolder.getUser().getId());
            }else {
                question.setUserId(QiuzhiUtils.ANONYMOUS_USERID);
            }
            question.setTitle(title);
            question.setDescription(description);
            question.setReward(Integer.parseInt(reward));
            question.setLabel(LabelKeyUtil.getLabel(label,university,industry));

            System.out.println(question);

            if (questionService.addQuestion(question) > 0){

               /* eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION)
                        .setActorId(hostHolder.getUser().getId()).setEntityId(question.getId())
                        .setExt("title",question.getTitle()).setExt("description",question.getDescription()));
*/
                return QiuzhiUtils.getJSONString(0,"添加成功");
            }

        } catch (Exception e) {
            logger.error("添加问题："+e.getMessage());
        }

        return QiuzhiUtils.getJSONString(1,"添加失败");
    }

    @RequestMapping("/toAdd")
    public String toAdd(){
        return "/jie/add";
    }

}
