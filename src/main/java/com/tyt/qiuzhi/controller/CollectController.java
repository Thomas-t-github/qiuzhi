package com.tyt.qiuzhi.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.tyt.qiuzhi.model.*;
import com.tyt.qiuzhi.service.CollectService;
import com.tyt.qiuzhi.service.QuestionService;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/collect")
public class CollectController {

    @Autowired
    CollectService collectService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;


    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addCollect(@RequestParam("qid") int qid){

        if (hostHolder.getUser() == null){
            return QiuzhiUtils.getJSONString(1,"请先登录再收藏！");
        }
        Collect collect = new Collect();
        collect.setUserId(hostHolder.getUser().getId());
        collect.setCreatedDate(new Date());
        collect.setEntityId(qid);
        collect.setEntityType(EntityType.ENTITY_QUESTION);
        collect.setStatus(0);

        collectService.addCollect(collect);

        return QiuzhiUtils.getJSONString(0,"收藏成功");
    }

    @RequestMapping(value = "/remove",method = RequestMethod.POST)
    @ResponseBody
    public String removeCollect(@RequestParam("qid") int qid){

        if (hostHolder.getUser() == null){
            return QiuzhiUtils.getJSONString(1,"请先登录再取消收藏！");
        }

        collectService.updateStatus(qid,EntityType.ENTITY_QUESTION,1);

        return QiuzhiUtils.getJSONString(0,"移除收藏成功");
    }

    @RequestMapping(value = "/find")
    public String findCollects(Model model){

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
