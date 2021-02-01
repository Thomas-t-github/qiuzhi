package com.tyt.qiuzhi.controller;

import com.tyt.qiuzhi.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/reg"}, method = {RequestMethod.POST})
    public String reg(Model model, @RequestParam("email") String email,
                      @RequestParam("username") String username,
                      @RequestParam("pass") String password,
                      @RequestParam("repass") String repassword,
                      @RequestParam("vercode") String vercode,
                      //@RequestParam("next") String next,
                      @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                      HttpServletResponse response) {

        try {
            int expiredTime = 1;
            if (rememberme == true) {
                expiredTime = 5;
            }
            Map<String, Object> map = userService.register(email, password, username, expiredTime);

            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme == true) {
                    cookie.setMaxAge(3600 * 24 * expiredTime);
                }
                response.addCookie(cookie);

                /*if (StringUtils.isNotBlank(next)){
                    return "redirect:/";
                }*/
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "/user/reg";
            }
        } catch (Exception e) {
            logger.error("注册异常：" + e.getMessage());
            model.addAttribute("msg", "服务器错误");
            return "/user/reg";
        }

    }

    @RequestMapping("/toReg")
    public String toRegister(){
        return "/user/reg";
    }

}