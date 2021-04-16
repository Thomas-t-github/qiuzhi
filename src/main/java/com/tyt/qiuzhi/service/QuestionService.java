package com.tyt.qiuzhi.service;

import com.tyt.qiuzhi.dao.QuestionDAO;
import com.tyt.qiuzhi.model.EntityType;
import com.tyt.qiuzhi.model.Question;
import com.tyt.qiuzhi.util.JedisAdapter;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    @Autowired
    CommentService commentService;

    @Autowired
    JedisAdapter jedisAdapter;

    public int addQuestion(Question question){
        //处理HTML标签
        question.setDescription(HtmlUtils.htmlEscape(question.getDescription()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        //处理敏感词

        List<Object> descList = sensitiveService.filter(question.getDescription());
        List<Object> titleList = sensitiveService.filter(question.getTitle());
        question.setDescription(descList.get(0).toString());
        question.setTitle(titleList.get(0).toString());

        int i = questionDAO.addQuestion(question);

        if ((Boolean) descList.get(1) || (Boolean) titleList.get(1)) {
            jedisAdapter.zadd(RedisKeyUtil.getIsAllowQuestion(), new Date().getTime(), String.valueOf(question.getId()));
        }

        return i;
    }

    public int selectQuestionsCount(){
        return questionDAO.selectQuestionsCount();
    }

    public boolean updateDescription(int id,String description){
        return questionDAO.updateDescription(id,description);
    }

    public List<Question> selectAll(){
        return questionDAO.selectAll();
    }

    public List<Question> selectByLabel(String label,int offset,int limit){
        return questionDAO.selectByLabel(label,offset,limit);
    }

    public boolean deleteQuestion(int id){
        commentService.deleteComment(id, EntityType.ENTITY_QUESTION);
        return questionDAO.deleteQuestion(id);
    }

    public List<Question> selectLatestQuestions(int userId, int offset, int limit){
        return questionDAO.selectLatestQuestions(userId,offset,limit);
    }

    public int updateCommentCount(int id,int commentCount){
        return questionDAO.updateCommentCount(id,commentCount);
    }

    public Question selectById(int id){
        return questionDAO.selectById(id);
    }

}
