package com.tyt.qiuzhi.controller;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import com.tyt.qiuzhi.asyncmq.EventModel;
import com.tyt.qiuzhi.asyncmq.EventProducer;
import com.tyt.qiuzhi.asyncmq.EventType;
import com.tyt.qiuzhi.model.EntityType;
import com.tyt.qiuzhi.model.User;
import com.tyt.qiuzhi.service.OauthServer;
import com.tyt.qiuzhi.service.UserService;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
public class QQController {
    private static final Logger logger = LoggerFactory.getLogger(QQController.class);


    @Autowired
    UserService userService;

    @Autowired
    OauthServer oauthServer;

    @Autowired
    EventProducer eventProducer;


    @RequestMapping("/loginByQQ")
    public void loginByQQ(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        try {
            response.sendRedirect(new Oauth().getAuthorizeURL(request));
            System.out.println("请求QQ登录，开始跳转");
        } catch (QQConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/qqcallback")
    public String connection(HttpServletRequest request, HttpServletResponse response, Map<String, Object> map) {

        try {
            //获取AccessToken
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
            String accessToken = null,
                    openID = null;
            long tokenExpireIn = 0L;

            if (accessTokenObj.getAccessToken().equals("")) {
                System.out.print("登录失败：没有获取到响应参数");
            } else {
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();
                System.out.println("accessToken=>" + accessTokenObj.getAccessToken());
                //request.getSession().setAttribute("demo_access_token", accessToken);
                //request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj = new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();
                //request.getSession().setAttribute("demo_openid", openID);


                //根据openid从MySQL数据库中获取userID
                com.tyt.qiuzhi.model.Oauth oauth = oauthServer.selectByEntity(openID, EntityType.ENTITY_QQ);
                int userId = -1;
                if (oauth == null){
                    User user = oauthServer.addOauth(accessToken, openID, EntityType.ENTITY_QQ);
                    userId = user.getId();

                    eventProducer.fireEvent("message",new EventModel(EventType.QQ_LOGIN)
                            .setActorId(QiuzhiUtils.SYSTEM_USERID).setEntityOwnerId(userId)
                            .setExt("content","尊敬的用户"+user.getNickName()+",恭喜您于"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"通过QQ第三方应用成功在本网站注册，默认密码是12345678，请尽快修改密码和修改邮箱，以免对您的正常使用造成影响！"));
                }else {
                    userId = oauth.getUserId();
                    User user = userService.selectById(userId);

                    eventProducer.fireEvent("message",new EventModel(EventType.QQ_LOGIN)
                            .setActorId(QiuzhiUtils.SYSTEM_USERID).setEntityOwnerId(userId)
                            .setExt("content","尊敬的用户"+user.getNickName()+" ,您于"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"通过QQ第三方授权登录本网站，如果未绑定有效邮箱，请到我的资料进邮箱修改验证，以免对您的正常使用造成影响,如已修改，请忽略本次提示！"));

                }
                if (userId < 0){
                    throw new RuntimeException("QQ授权登录出现异常");
                }
                String loginTicket = userService.addLoginTicket(userId, 10);
                if (loginTicket != null) {
                    Cookie cookie = new Cookie("ticket", loginTicket);
                    cookie.setPath("/");
                    cookie.setMaxAge(3600 * 24 * 10);
                    response.addCookie(cookie);
                }else {
                    throw new RuntimeException("QQ授权登录出现异常");
                }



            }
        } catch (QQConnectException e) {

        }
        return "redirect:/";
    }

/*
    //官方demo
    @RequestMapping("/qqcallback")
    public void connection(HttpServletRequest request,HttpServletResponse response,Map<String,Object> map){
        response.setContentType("text/html; charset=utf-8");

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);

            String accessToken   = null,
                    openID        = null;
            long tokenExpireIn = 0L;


            if (accessTokenObj.getAccessToken().equals("")) {
                System.out.print("登录失败：没有获取到响应参数");
                //System.out.println("accessTokenObj=>"+accessTokenObj+" ; accessToken=>"+accessTokenObj.getAccessToken());
                //return "accessTokenObj=>"+accessTokenObj+" ; accessToken=>"+accessTokenObj.getAccessToken();
            } else {
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();
                System.out.println("accessToken=>"+accessTokenObj.getAccessToken());
                request.getSession().setAttribute("demo_access_token", accessToken);
                request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj =  new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();

                out.println("欢迎你，代号为 " + openID + " 的用户!");
                request.getSession().setAttribute("demo_openid", openID);
                // 利用获取到的accessToken 去获取当前用户的openid --------- end


                out.println("<p> start -----------------------------------利用获取到的accessToken,openid 去获取用户在Qzone的昵称等信息 ---------------------------- start </p>");
                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                out.println("<br/>");
                if (userInfoBean.getRet() == 0) {
                    out.println(userInfoBean.getNickname() + "<br/>");
                    out.println(userInfoBean.getGender() + "<br/>");
                    out.println("黄钻等级： " + userInfoBean.getLevel() + "<br/>");
                    out.println("会员 : " + userInfoBean.isVip() + "<br/>");
                    out.println("黄钻会员： " + userInfoBean.isYellowYearVip() + "<br/>");
                    out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL30() + "><br/>");
                    out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL50() + "><br/>");
                    out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL100() + "><br/>");
                } else {
                    out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg());
                }

            }
        } catch (QQConnectException e) {

        }
        //return null;
    }
*/

}
