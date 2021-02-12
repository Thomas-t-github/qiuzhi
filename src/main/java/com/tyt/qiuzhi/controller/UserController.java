package com.tyt.qiuzhi.controller;

import com.tyt.qiuzhi.model.*;
import com.tyt.qiuzhi.service.CollectService;
import com.tyt.qiuzhi.service.CommentService;
import com.tyt.qiuzhi.service.QuestionService;
import com.tyt.qiuzhi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CollectService collectService;

    @RequestMapping(value = "/homepage/{uid}", method = RequestMethod.GET)
    public String homepage(Model model, @PathVariable("uid") int uid){

        ArrayList<ViewObject> vos = new ArrayList<>();

        User user = userService.selectById(uid);
        List<Question> questions = questionService.selectLatestQuestions(user.getId(), 0, 10);

        model.addAttribute("owner",user);
        model.addAttribute("questions",questions);

        List<Comment> comments = commentService.selectByUserId(user.getId());

        for (Comment comment : comments) {
            ViewObject vo = new ViewObject();
            Question question = questionService.selectById(comment.getEntityId());
            vo.set("comment",comment);
            vo.set("question",question);
            vos.add(vo);
        }

        model.addAttribute("vos",vos);
        return "user/home";
    }


    @RequestMapping(value = "/homepage", method = RequestMethod.GET)
    public String homepageByName(@RequestParam("username") String username){
        return "user/home";
    }

    @RequestMapping(value = "/toSet", method = RequestMethod.GET)
    public String toSet(){
        return "user/set";
    }

    @RequestMapping(value = "/toMessage", method = {RequestMethod.GET,RequestMethod.POST})
    public String toMessage(){
        return "user/message";
    }

    @RequestMapping(value = "/toUserCenter", method = RequestMethod.GET)
    public String toUserCenter(Model model){

        if (hostHolder.getUser() == null){
            return "redirect:/user/toLogin";
        }

        ArrayList<ViewObject> vos = new ArrayList<>();

        List<Collect> collects = collectService.selectByUserId(hostHolder.getUser().getId());


        for (Collect collect : collects) {
            ViewObject vo = new ViewObject();
            Question question = questionService.selectById(collect.getEntityId());
            vo.set("collect",collect);
            vo.set("question",question);
            vos.add(vo);
        }

        List<Question> questions = questionService.selectLatestQuestions(hostHolder.getUser().getId(), 0, 10);

        model.addAttribute("questions",questions);

        model.addAttribute("vos",vos);

        return "user/index";
    }

}
