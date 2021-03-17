package com.tyt.qiuzhi.controller;

import com.tyt.qiuzhi.async.EventModel;
import com.tyt.qiuzhi.async.EventProducer;
import com.tyt.qiuzhi.async.EventType;
import com.tyt.qiuzhi.service.UserService;
import com.tyt.qiuzhi.util.JedisAdapter;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping(path = {"/reg"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map reg( @RequestParam("email") String email,
                      @RequestParam("nickname") String nickName,
                      @RequestParam("pass") String password,
                      @RequestParam("repass") String repassword,
                      @RequestParam("vercode") String vercode,
                      //@RequestParam("next") String next,
                      @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                      HttpServletResponse response) {


        HashMap<String, Object> result = new HashMap<>();

        if (!password.equals(repassword)){
            result.put("status",1);
            result.put("msg","两次密码不一致，请重新输入！");
            return result;
        }

        if ("".equals(vercode)){
            result.put("status",1);
            result.put("msg","请输入验证码！");
            return result;
        }

        String code = jedisAdapter.get(RedisKeyUtil.getVerCodeKey(email));
        if (code == null){
            result.put("status",2);
            result.put("msg","请先获取验证码！");
            return result;
        }
        if (!vercode.equals(code)){
            result.put("status",3);
            result.put("msg","验证码错误！");
            return result;
        }

        int expiredTime = 1;
        if (rememberme == true) {
            expiredTime = 5;
        }

        Map<String, Object> map = userService.register(email, password, nickName, expiredTime);

        try {



            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");

                cookie.setMaxAge(3600 * 24 * expiredTime);

                response.addCookie(cookie);

                /*if (StringUtils.isNotBlank(next)){
                    return "redirect:/";
                }*/
                //return "redirect:/";
                result.put("status",0);
                result.put("msg","注册成功！");
                return result;

            } else {
                map.put("msg", map.get("msg"));
                return map;
            }
        } catch (Exception e) {
            logger.error("注册异常：" + e.getMessage());
            //model.addAttribute("msg", "服务器错误");
            map.put("msg", "服务器错误");
            return map;
        }

    }

    @RequestMapping("/toReg")
    public String toRegister(){
        return "/user/reg";
    }

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "/user/login";
    }

    @RequestMapping(path = {"/login"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map login( @RequestParam("email") String email,
                        @RequestParam("pass") String password,
                        //@RequestParam("vercode") String vercode,
                        @RequestParam(value = "next",defaultValue = "") String next,
                        @RequestParam(value="rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response){

        Map<String, Object> result = new HashMap<>();

        /*if ("".equals(vercode)){
            result.put("status",1);
            result.put("msg","请输入验证码！");
            return result;
        }

        String code = jedisAdapter.get(RedisKeyUtil.getVerCodeKey(email));
        if (code == null){
            result.put("status",2);
            result.put("msg","请先获取验证码！");
            return result;
        }
        if (!vercode.equals(code)){
            result.put("status",3);
            result.put("msg","验证码错误！");
            return result;
        }*/

        try {
            int expiredTime = 1;
            if (rememberme == true){
                expiredTime = 5;
            }
            Map<String, Object> map = userService.login(email, password,expiredTime);

            if (map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");

                cookie.setMaxAge(3600*24*expiredTime);

                response.addCookie(cookie);

                //异步
                /*String ip = IpUtils.getIpAddr(request);
                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExt("email","2360564158@qq.com")
                        .setExt("username",username)
                        .setExt("ip",ip));

                */
                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExt("email","2360564158@qq.com"));



                /*if (StringUtils.isNotBlank(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";*/

                result.put("status",0);
                result.put("msg","登录成功！");
                if (!"".equals(next)){
                    result.put("next",next);
                }

                return result;
            }else {
                result.put("msg",map.get("msg"));
                return result;
            }
        } catch (Exception e) {
            logger.error("登录异常："+e.getMessage());
            result.put("msg","服务器错误");
            return result;
        }

    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }

    @RequestMapping(path = {"/relogin"}, method = {RequestMethod.GET})
    public String relogin(Model model, @RequestParam("next") String next){
        model.addAttribute("next",next);
        return "/user/login";
    }

}