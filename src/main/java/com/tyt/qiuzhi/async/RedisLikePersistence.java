package com.tyt.qiuzhi.async;

import com.tyt.qiuzhi.model.EntityType;
import com.tyt.qiuzhi.service.CommentService;
import com.tyt.qiuzhi.service.LikeService;
import com.tyt.qiuzhi.util.JedisAdapter;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RedisLikePersistence implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RedisLikePersistence.class);

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;

    @Override
    public void afterPropertiesSet() throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    String setKey = RedisKeyUtil.getLikeCountSetKey();
                    String commentId = jedisAdapter.spop(setKey);
                    if (commentId == null) {
                        try {
                            Thread.sleep(1000 * 60);
                        } catch (InterruptedException e) {
                            logger.error("Redis持久化LikeCount失败：" + e.getMessage());
                        }
                    } else {
                        long likeCount = likeService.getLikeCount(EntityType.ENTITY_COMMENT, Integer.parseInt(commentId));
                        commentService.updateLikeCount(Integer.parseInt(commentId), (int) likeCount);
                    }
                }
            }
        }).start();

    }
}
