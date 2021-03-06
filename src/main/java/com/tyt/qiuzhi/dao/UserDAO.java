package com.tyt.qiuzhi.dao;

import com.tyt.qiuzhi.model.User;
import org.apache.ibatis.annotations.*;

public interface UserDAO {

    String TABLE_NAME = " user ";
    String INSERT_FIELDS = " email, password, nick_name, salt, city, sex, sign, created_date, head_url ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{email},#{password},#{nickName},#{salt},#{city},#{sex},#{sign},#{createdDate},#{headUrl})"})
    int addUser(User user);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    User selectById(int id);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where email=#{email}"})
    User selectByEmail(String email);

    @Update({"update ", TABLE_NAME, " set password=#{password} where id=#{id}"})
    int updatePassword(User user);

    @Update({"update ", TABLE_NAME, " set head_url=#{headUrl} where id=#{id}"})
    int updateHeadUrl(@Param("id") int id, @Param("headUrl") String headUrl);

    @Update({"update ", TABLE_NAME, " set email=#{email},nick_name=#{nickName},city=#{city},sex=#{sex},sign=#{sign} where id=#{id}"})
    int updateProfile(User user);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    int deleteById(int id);

}
