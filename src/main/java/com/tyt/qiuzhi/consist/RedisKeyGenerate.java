package com.tyt.qiuzhi.consist;

import com.tyt.qiuzhi.controller.QQController;
import com.tyt.qiuzhi.util.JedisAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyGenerate implements KeyGenerate {
    private static final Logger logger = LoggerFactory.getLogger(RedisKeyGenerate.class);

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public Long generateLongKey(String redisKey) {

        try {
            Long key = jedisAdapter.incr(redisKey, 1l);
            if(key != null){
                return key;
            }
        } catch (Exception e) {
            logger.error("获取自增Key时Redis异常:"+e.getMessage());
        }

        return new BaseKeyGenerate().generateLongKey(redisKey);
    }
}
