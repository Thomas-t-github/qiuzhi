package com.tyt.qiuzhi.service;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.tyt.qiuzhi.consist.RedisKeyGenerate;
import com.tyt.qiuzhi.dao.OauthDAO;
import com.tyt.qiuzhi.model.Oauth;
import com.tyt.qiuzhi.model.User;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
public class OauthServer {

    @Autowired
    OauthDAO oauthDAO;

    @Autowired
    RedisKeyGenerate redisKeyGenerate;

    @Autowired
    UserService userService;

    public User addOauth(String accessToken, String openId, int appType) throws QQConnectException {

        User user = new User();
        //start ----利用获取到的accessToken,openid 去获取用户在Qzone的昵称等信息 ------- start
        UserInfo qzoneUserInfo = new UserInfo(accessToken, openId);
        UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
        if (userInfoBean.getRet() == 0) {

            user.setNickName(userInfoBean.getNickname());
            user.setSex("男".equals(userInfoBean.getGender()) ? 0 : 1);
            user.setSalt(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8));
            user.setEmail(redisKeyGenerate.generateLongKey(RedisKeyUtil.getBizRedisIncrKey())+"@qq.com");
            user.setHeadUrl(userInfoBean.getAvatar().getAvatarURL100());
            user.setPassword(QiuzhiUtils.MD5("12345678" + user.getSalt()));
            user.setCreatedDate(new Date());

            userService.addUser(user);

            Oauth oauth = new Oauth();
            oauth.setCreatedDate(new Date());
            oauth.setAppType(appType);
            oauth.setOpenId(openId);
            oauth.setUserId(user.getId());
            oauthDAO.addOauth(oauth);
            return user;
        } else {
            System.out.println(("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg()));
        }

        return null;
    }

    public Oauth selectByEntity(String openId, int appType){
        return oauthDAO.selectByEntity(openId,appType);
    }

}
