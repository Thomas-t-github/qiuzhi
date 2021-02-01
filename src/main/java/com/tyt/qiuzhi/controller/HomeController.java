package com.tyt.qiuzhi.controller;

import com.tyt.qiuzhi.model.HostHolder;
import com.tyt.qiuzhi.model.Question;
import com.tyt.qiuzhi.model.User;
import com.tyt.qiuzhi.model.ViewObject;
import com.tyt.qiuzhi.service.QuestionService;
import com.tyt.qiuzhi.service.UserService;
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
    HostHolder hostHolder;

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model){

        List<ViewObject> questions = getQuestions(0, 0, 10);
        model.addAttribute("vos",questions);
        return "index";
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

}
