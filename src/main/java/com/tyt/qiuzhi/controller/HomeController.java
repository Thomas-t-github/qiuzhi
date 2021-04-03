package com.tyt.qiuzhi.controller;

import com.alibaba.fastjson.JSONObject;
import com.tyt.qiuzhi.model.*;
import com.tyt.qiuzhi.service.CommentService;
import com.tyt.qiuzhi.service.QuestionService;
import com.tyt.qiuzhi.service.UserService;
import com.tyt.qiuzhi.util.JedisAdapter;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model){

        List<ViewObject> questions = getQuestions(0, 0, 20);
        String advertisementJson = jedisAdapter.get(RedisKeyUtil.getBizAdvertisementKey());
        if (advertisementJson != null){
            Advertisement advertisement = JSONObject.parseObject(advertisementJson, Advertisement.class);
            model.addAttribute("advertisement",advertisement);
        }
        model.addAttribute("vos",questions);
        return "index";
    }

    @RequestMapping(path = {"/queryByLabel"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String queryByLabel(Model model,@RequestParam("label") String label,
                               @RequestParam(value = "offset",defaultValue = "0") int offset,
                               @RequestParam(value = "limit",defaultValue = "20") int limit){

        List<ViewObject> questions = getQuestions(label,offset,limit);
        String advertisementJson = jedisAdapter.get(RedisKeyUtil.getBizAdvertisementKey());
        if (advertisementJson != null){
            Advertisement advertisement = JSONObject.parseObject(advertisementJson, Advertisement.class);
            model.addAttribute("advertisement",advertisement);
        }
        model.addAttribute("vos",questions);
        return "index";
    }

    private List<ViewObject> getQuestions(String label,int offset,int limit){
        List<ViewObject> vos = new ArrayList<>();
        List<Question> questions = questionService.selectByLabel(label,offset,limit);
        for (Question question : questions) {
            ViewObject vo = new ViewObject();
            User user = userService.selectById(question.getUserId());
            String description = question.getDescription();
            question.setDescription(description.substring(0, description.length() > 100 ? 100 : description.length()));
            vo.set("question",question);
            vo.set("user",user);
            vos.add(vo);
        }
        return vos;
    }


    private List<ViewObject> getQuestions(int userId,int offset,int limit){
        List<ViewObject> vos = new ArrayList<>();
        List<Question> questions = questionService.selectLatestQuestions(userId, offset, limit);
        for (Question question : questions) {
            ViewObject vo = new ViewObject();
            User user = userService.selectById(question.getUserId());
            String description = question.getDescription();
            question.setDescription(description.substring(0, description.length() > 100 ? 100 : description.length()));
            vo.set("question",question);
            //vo.set("followCount",followService.getFollowerCount(EntityType.ENTITY_QUESTION,question.getId()));
            vo.set("user",user);
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = "/dynamic", method = {RequestMethod.GET, RequestMethod.POST})
    public String dynamic(Model model){

        ArrayList<ViewObject> vos = new ArrayList<>();


        List<Comment> comments = commentService.selectByUserId(hostHolder.getUser() != null ? hostHolder.getUser().getId() : 3);
        for (Comment comment : comments) {
            ViewObject vo = new ViewObject();
            Question question = questionService.selectById(comment.getEntityId());
            vo.set("comment",comment);
            vo.set("question",question);
            vos.add(vo);
        }

        model.addAttribute("vos",vos);
        return "dynamic";
    }

}
