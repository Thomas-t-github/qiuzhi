package com.tyt.qiuzhi.controller;

import com.tyt.qiuzhi.async.EventModel;
import com.tyt.qiuzhi.async.EventProducer;
import com.tyt.qiuzhi.async.EventType;
import com.tyt.qiuzhi.model.*;
import com.tyt.qiuzhi.service.*;
import com.tyt.qiuzhi.util.JedisAdapter;
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
    CommentService commentService;

    @Autowired
    CollectService collectService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(value = "/detail/{qid}", method = {RequestMethod.GET})
    public String questionDetail(Model model, @PathVariable("qid") int qid){

        ArrayList<ViewObject> vos = new ArrayList<>();

        int userId = 0;
        List<Collect> isCollect = null;
        if (hostHolder.getUser() != null){
            userId = hostHolder.getUser().getId();
            isCollect = collectService.getUserCollectStatus(hostHolder.getUser().getId(), qid, EntityType.ENTITY_QUESTION);
        }

        Question question = questionService.selectById(qid);
        User owner = userService.selectById(question.getUserId());
        List<Comment> comments = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        for (Comment comment : comments) {
            ViewObject vo = new ViewObject();
            User user = userService.selectById(comment.getUserId());
            int likeStatus = likeService.getLikeStatus(userId, EntityType.ENTITY_COMMENT, comment.getId());
            vo.set("comment",comment);
            vo.set("likeStatus",likeStatus);
            vo.set("commentOwner",user);
            vos.add(vo);
        }
        model.addAttribute("isCollect",isCollect.size() > 0 ? isCollect.get(0) : null);

        model.addAttribute("question",question);
        model.addAttribute("vos",vos);
        model.addAttribute("owner",owner);


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
