package com.tyt.qiuzhi.asyncmq.consumer;

import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSON;
import com.tyt.qiuzhi.asyncmq.EventModel;
import com.tyt.qiuzhi.controller.LoginController;
import com.tyt.qiuzhi.model.User;
import com.tyt.qiuzhi.service.UserService;
import com.tyt.qiuzhi.util.JedisAdapter;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Random;

@Component
@RabbitListener(queues = {"email.direct.queue"})
public class EmailConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EmailConsumer.class);

    @Autowired
    UserService userService;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    JedisAdapter jedisAdapter;

    @RabbitHandler
    public void doHandler(String eventModelString){

        EventModel eventModel = JSON.parseObject(eventModelString, EventModel.class);

        HashMap<String, Object> map = new HashMap<>();

        String vercode = String.format("%04d", new Random().nextInt(9999));

        map.put("vercode",vercode);

        map.put("username",eventModel.getExt("nickName"));


        Context context = new Context();
        context.setVariables(map);
        String process = templateEngine.process("mail/vercode", context);

        try {
            MailUtil.send(eventModel.getExt("email"), "职享网", process, true);
        } catch (Exception e) {
            logger.error("发送邮件验证码失败："+e.getMessage());
            return;
        }

        jedisAdapter.setex(RedisKeyUtil.getVerCodeKey(eventModel.getExt("email")),60*5,vercode);

    }

}
