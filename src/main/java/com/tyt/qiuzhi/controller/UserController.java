package com.tyt.qiuzhi.controller;

import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSONObject;
import com.tyt.qiuzhi.model.*;
import com.tyt.qiuzhi.service.*;
import com.tyt.qiuzhi.util.JedisAdapter;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    QiniuService qiniuService;

    @RequestMapping(value = "/setNewPassword",method = RequestMethod.POST)
    @ResponseBody
    public Map setNewPassword(@RequestParam("nowpass") String nowpass,
                                 @RequestParam("pass") String pass,
                                 @RequestParam("repass") String repass){

        Map<String,Object> map = userService.updatePassword(nowpass, pass, repass);
        if (map.containsKey("msg")){
            System.out.println(map.get("msg"));
            map.put("status",1);
            return map;
        }
        map.put("msg","密码修改成功");
        map.put("status",0);
        return map;
    }

    @RequestMapping(value = "/setProfile",method = RequestMethod.POST)
    @ResponseBody
    public String setProfile(@RequestParam("email") String email,
                             @RequestParam("username") String nickName,
                             @RequestParam("sex") int sex,
                             @RequestParam("city") String city,
                             @RequestParam("sign") String sign){
        if (hostHolder.getUser() == null){
            return "redirect:/user/toLogin";
        }

        User user = hostHolder.getUser();
        user.setNickName(nickName);
        user.setEmail(email);
        user.setSex(sex);
        user.setCity(city);
        user.setSign(sign);
        userService.updateProfile(user);
        return QiuzhiUtils.getJSONString(0);
    }


    @RequestMapping(value = "/upload/",method = RequestMethod.POST)
    @ResponseBody
    public String upLoadImage(@RequestParam("file") MultipartFile file){

        if (hostHolder.getUser() == null){
            return "redirect:/user/toLogin";
        }

        try {
            String fileUrl = qiniuService.saveImage(file);

            if(fileUrl == null){
                return QiuzhiUtils.getJSONString(1,"图片上传失败");
            }
            userService.updateHeadUrl(hostHolder.getUser().getId(),fileUrl);
            return QiuzhiUtils.getJSONString(0);

        } catch (IOException e) {
            logger.error("图片上传失败"+e.getMessage());
            return QiuzhiUtils.getJSONString(1,"图片上传失败");
        }
    }


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


    @RequestMapping(value = "/toSet", method = {RequestMethod.GET,RequestMethod.POST})
    public String toSet(){
        if (hostHolder.getUser() == null){
            return "redirect:/user/toLogin";
        }
        return "user/set";
    }


    @RequestMapping(value = "/sendEmail", method = RequestMethod.POST)
    @ResponseBody
    public String sendEmail(@RequestParam("email") String email){

        HashMap<String, Object> map = new HashMap<>();

        String vercode = String.format("%04d", new Random().nextInt(9999));

        map.put("vercode",vercode);
        User user = userService.selectByEmail(email);
        if (user != null){
            map.put("username",user.getNickName());
        }else {
            map.put("username",email);
        }

        Context context = new Context();
        context.setVariables(map);
        String process = templateEngine.process("mail/vercode", context);

        try {
            MailUtil.send(email, "职享网", process, true);
        } catch (Exception e) {
            return QiuzhiUtils.getJSONString(1,"发送验证码失败："+(e.getMessage().contains("Invalid Addresses") ? "非法邮箱，请输入有效邮箱" : e.getMessage()));
        }

        jedisAdapter.setex(RedisKeyUtil.getVerCodeKey(email),60*5,vercode);

        return QiuzhiUtils.getJSONString(0,"验证码发送成功");
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
