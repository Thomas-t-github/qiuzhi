package com.tyt.qiuzhi.manage;

import com.tyt.qiuzhi.async.EventModel;
import com.tyt.qiuzhi.async.EventProducer;
import com.tyt.qiuzhi.async.EventType;
import com.tyt.qiuzhi.model.EntityType;
import com.tyt.qiuzhi.model.Question;
import com.tyt.qiuzhi.service.QuestionService;
import com.tyt.qiuzhi.util.JedisAdapter;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/manage")
public class AllowNewQuestionController {


    @Autowired
    QuestionService questionService;

    @Autowired
    JedisAdapter jedisAdapter;

    /*@Autowired
    EventProducer eventProducer;*/


    @RequestMapping(value = "/allowQuestion",method = RequestMethod.GET,produces={"application/json;charset=UTF-8"})
    @ResponseBody
    public String allowQuestion(@RequestParam("id") int id){
        if (jedisAdapter.zrem(RedisKeyUtil.getIsAllowQuestion(),String.valueOf(id)) > 0){
            return QiuzhiUtils.getJSONString(0,"审核成功");
        }else {
            return QiuzhiUtils.getJSONString(1,"审核帖子失败");
        }
    }

    @RequestMapping(value = "/deleteSensitiveQuestion",method = RequestMethod.GET,produces={"application/json;charset=UTF-8"})
    @ResponseBody
    public String deleteSensitiveQuestion(@RequestParam("id") int id){

        Question question = questionService.selectById(id);

        if (questionService.deleteQuestion(id)){
            jedisAdapter.zrem(RedisKeyUtil.getIsAllowQuestion(),String.valueOf(id));


            //异步
            /*eventProducer.fireEvent(new EventModel(EventType.VIOLATION_QUESTION)
            .setActorId(QiuzhiUtils.SYSTEM_USERID).setEntityId(id)
            .setEntityType(EntityType.ENTITY_QUESTION).setEntityOwnerId(question.getUserId())
            .setExt("createdDate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(question.getCreatedDate()))
            .setExt("questionTitle",question.getTitle()));*/

            return QiuzhiUtils.getJSONString(0,"删除违规贴成功");
        }else {
            return QiuzhiUtils.getJSONString(1,"删除违规帖失败");
        }

    }

    @RequestMapping(value = {"/toNewQuestionPage"})
    public String toNewQuestionPage(){
        return "manage/allowNewQuestion";
    }

    @RequestMapping(value = "/allowNewQuestionsData",method = RequestMethod.GET)
    @ResponseBody
    public Map showNeedAllowQuestions(@RequestParam("page") int page, @RequestParam("limit") int limit){
        Map<String, Object> result = new HashMap<>();

        int offset = (page - 1) * limit;

        Object[] questionIds = jedisAdapter.zrange(RedisKeyUtil.getIsAllowQuestion(), offset, limit - 1).toArray();
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < questionIds.length; i++) {
            Question question = questionService.selectById(Integer.parseInt((String) questionIds[i]));
            questions.add(question);
        }
        long count = jedisAdapter.zcard(RedisKeyUtil.getIsAllowQuestion());
        result.put("code",0);
        result.put("msg","");
        result.put("count",count);
        result.put("data",questions);

        return result;
    }

}
