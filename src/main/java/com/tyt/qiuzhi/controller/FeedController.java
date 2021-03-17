package com.tyt.qiuzhi.controller;

import com.tyt.qiuzhi.model.Feed;
import com.tyt.qiuzhi.model.HostHolder;
import com.tyt.qiuzhi.service.FeedService;
import com.tyt.qiuzhi.service.FollowService;
import com.tyt.qiuzhi.util.JedisAdapter;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/feed")
public class FeedController {

    private static final Logger logger = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    FeedService feedService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    FollowService followService;


    @RequestMapping(path = {"/pushfeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    private String getPushFeeds(Model model){

        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);
        ArrayList<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            Feed feed = feedService.selectFeedById(Integer.parseInt(feedId));
            if (feed != null){
                feeds.add(feed);
            }
        }
        model.addAttribute("feeds",feeds);
        return "dynamic";
    }


}
