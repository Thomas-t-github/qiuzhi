package com.tyt.qiuzhi.util;

public class RedisKeyUtil {

    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";
    private static String BIZ_FOLLOWER = "FOLLOWER";
    private static String BIZ_FOLLOWEE = "FOLLOWEE";
    private static String BIZ_TIMELINE = "TIMELINE";
    private static String BIZ_UPDATE_LIKECOUNT = "UPDATE_LIKECOUNT_SET";
    private static String BIZ_VERCODE = "BIZ_VERCODE";
    private static String BIZ_IS_ALLOW_QUESTION = "IS_ALLOW_QUESTION";
    private static String BIZ_MANAGER_INFO_KEY = "MANAGER_INFO_KEY";
    private static String BIZ_ADVERTISEMENT_KEY = "ADVERTISEMENT_KEY";
    private static String BIZ_ADVERTISEMENT_IMAGE_KEY = "ADVERTISEMENT_IMAGE_KEY";


    public static String getVerCodeKey(String email){
        return BIZ_VERCODE+SPLIT+email;
    }

    public static String getBizAdvertisementKey(){
        return BIZ_ADVERTISEMENT_KEY;
    }
    public static String getBizAdvertisementImageKey(){
        return BIZ_ADVERTISEMENT_IMAGE_KEY;
    }

    public static String getManagerInfoKey(String managerName){
        return BIZ_MANAGER_INFO_KEY+SPLIT+managerName;
    }

    public static String getLikeCountSetKey(){
        return BIZ_UPDATE_LIKECOUNT;
    }

    public static String getLikeKey(int entityType,int entityId){
        return BIZ_LIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
    }
    public static String getDislikeKey(int entityType,int entityId){
        return BIZ_DISLIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
    }

    public static String getEventQueueKey(){
        return BIZ_EVENTQUEUE;
    }
    public static String getIsAllowQuestion(){
        return BIZ_IS_ALLOW_QUESTION;
    }

    public static String getFollowerKey(int entityType,int entityId){
        return BIZ_FOLLOWER+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
    }

    public static String getFolloweeKey(int userId,int entityType){
        return BIZ_FOLLOWEE+SPLIT+String.valueOf(userId)+SPLIT+String.valueOf(entityType);
    }

    public static String getTimelineKey(int userId){
        return BIZ_TIMELINE+SPLIT+String.valueOf(userId);
    }

}
