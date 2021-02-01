package com.tyt.qiuzhi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

@Controller
public class TestController {

    @RequestMapping(value = {"/","/index"})
    public String index(){
        return "index";
    }

    /*public static void main(String[] args) {

        Jedis jedis = new Jedis("47.96.97.43",6379);
        jedis.auth("tytredispassword");

        System.out.println(jedis.get("zhangsan"));

    }*/

}
