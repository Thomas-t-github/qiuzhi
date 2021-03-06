package com.tyt.qiuzhi;

import com.tyt.qiuzhi.dao.QuestionDAO;
import com.tyt.qiuzhi.dao.UserDAO;
import com.tyt.qiuzhi.model.User;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Date;
import java.util.Random;
import java.util.UUID;


@SpringBootTest
public class InitDatabaseTests {

    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    UserDAO userDAO;

    @Test
    void init_data(){

        /*for (int i = 1; i <= 10; i++) {
            Question question = new Question();
            question.setTitle("测试"+i);
            question.setDescription("标题描述"+i);
            question.setLabel("提问");
            question.setReward(20);
            question.setUserId(i);
            Date date = new Date(new Date().getTime() + 1000 * 60 * 60 * i);
            question.setCreatedDate(date);
            question.setCommentCount(i);

            questionDAO.addQuestion(question);
        }*/

        for (int i = 1; i <= 10; i++) {
            User user = new User();

            user.setEmail("12345678"+i+"@qq.com");
            user.setNickName("user"+i);
            user.setSalt(UUID.randomUUID().toString().replaceAll("-","").substring(0,8));
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
            user.setPassword(QiuzhiUtils.MD5(123+user.getSalt()));
            user.setCreatedDate(new Date());

            userDAO.addUser(user);
        }


    }

}
