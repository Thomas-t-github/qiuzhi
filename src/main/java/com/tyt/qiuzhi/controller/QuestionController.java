package com.tyt.qiuzhi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/jie")
public class QuestionController {


    @RequestMapping("/addQuestion")
    public String addQuestion(){
        return "/";
    }

    @RequestMapping("/toAdd")
    public String toAdd(){
        return "/jie/add";
    }

}
