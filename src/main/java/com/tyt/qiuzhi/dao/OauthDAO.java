package com.tyt.qiuzhi.dao;


import com.tyt.qiuzhi.model.Oauth;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface OauthDAO {

    String TABLE_NAME = " oauth ";
    String INSERT_FIELDS = " user_id, open_id, app_type, created_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{openId},#{appType},#{createdDate})"})
    int addOauth(Oauth oauth);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where open_id=#{openId} and app_type=#{appType}"})
    Oauth selectByEntity(@Param("openId") String openId, @Param("appType") int appType);

}
