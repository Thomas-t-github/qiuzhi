package com.tyt.qiuzhi.consist;



public class BaseKeyGenerate implements KeyGenerate {
    @Override
    public Long generateLongKey(String redisKey) {
        return System.currentTimeMillis() * 1000000 + (int)(Math.random()*1000000);
    }
}
