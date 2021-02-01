package com.tyt.qiuzhi;

import com.tyt.qiuzhi.dao.QuestionDAO;
import com.tyt.qiuzhi.model.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Date;


@SpringBootTest
public class InitDatabaseTests {

    @Autowired
    QuestionDAO questionDAO;

    @Test
    void init_data(){

        for (int i = 1; i <= 10; i++) {
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
        }


    }

}
