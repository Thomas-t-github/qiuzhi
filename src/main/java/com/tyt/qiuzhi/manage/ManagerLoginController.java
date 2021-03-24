package com.tyt.qiuzhi.manage;

import com.tyt.qiuzhi.async.EventModel;
import com.tyt.qiuzhi.async.EventType;
import com.tyt.qiuzhi.controller.LoginController;
import com.tyt.qiuzhi.dao.LoginTicketDAO;
import com.tyt.qiuzhi.model.LoginTicket;
import com.tyt.qiuzhi.util.JedisAdapter;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/manage")
public class ManagerLoginController {
    private static final Logger logger = LoggerFactory.getLogger(ManagerLoginController.class);

    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "manage/login";
    }

    @RequestMapping(path = {"/relogin"}, method = {RequestMethod.GET})
    public String relogin(Model model, @RequestParam("next") String next){
        model.addAttribute("next",next);
        return "manage/login";
    }

    @RequestMapping(path = {"/login"}, method = {RequestMethod.POST})
    @ResponseBody
    public Map login(@RequestParam("email") String email,
                     @RequestParam("pass") String password,
                     //@RequestParam("vercode") String vercode,
                     @RequestParam(value = "next",defaultValue = "") String next,
                     HttpServletResponse response){

        Map<String, Object> result = new HashMap<>();

        try {
            int expiredTime = 1;

            if (email == null || email.equals("")){
                result.put("msg","账号不能为空");
                return result;
            }
            if (!"admin".equals(email)){
                result.put("msg","账号不存在");
                return result;
            }
            if (password == null || password.equals("")){
                result.put("msg","密码不能为空");
                return result;
            }
            String managerPassword = jedisAdapter.get(RedisKeyUtil.getManagerInfoKey(email));

            if (!password.equals(managerPassword)){
                result.put("msg","密码不正确");
                return result;
            }

            LoginTicket loginTicket = new LoginTicket();

            loginTicket.setUserId(0);
            loginTicket.setStatus(0);
            loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * 24 * expiredTime);
            loginTicket.setExpired(date);

            int i = loginTicketDAO.addTicket(loginTicket);

            if (i > 0){
                Cookie cookie = new Cookie("ticket",loginTicket.getTicket());
                cookie.setPath("/manage/");

                cookie.setMaxAge(3600*24*expiredTime);

                response.addCookie(cookie);


                result.put("status",0);
                result.put("msg","登录成功！");
                if (!"".equals(next)){
                    result.put("next",next);
                }

                return result;
            }else {
                result.put("msg","账号或密码错误");
                return result;
            }
        } catch (Exception e) {
            logger.error("登录异常："+e.getMessage());
            result.put("msg","服务器错误");
            return result;
        }

    }

}
