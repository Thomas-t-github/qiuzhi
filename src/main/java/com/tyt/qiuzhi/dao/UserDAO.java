package com.tyt.qiuzhi.dao;

import com.tyt.qiuzhi.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserDAO {

    String TABLE_NAME = " user ";
    String INSERT_FIELDS = " email, password, nick_name, salt, city, sex, sign, created_date, head_url ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{email},#{password},#{nickName},#{salt},#{city},#{sex},#{sign},#{createdDate},#{headUrl})"})
    int addUser(User user);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    User selectById(int id);

    @Select({"select count(id) from ", TABLE_NAME})
    int selectUsersCount();

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " ORDER BY created_date limit #{offset},#{limit}"})
    List<User> selectUsers(@Param("offset") int offset,@Param("limit") int limit);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where email=#{email}"})
    User selectByEmail(String email);

    @Update({"update ", TABLE_NAME, " set password=#{password} where id=#{id}"})
    int updatePassword(@Param("id") int id, @Param("password") String password);

    @Update({"update ", TABLE_NAME, " set head_url=#{headUrl} where id=#{id}"})
    int updateHeadUrl(@Param("id") int id, @Param("headUrl") String headUrl);

    @Update({"update ", TABLE_NAME, " set nick_name=#{nickName} where id=#{id}"})
    int updateNickName(@Param("id") int id, @Param("nickName") String nickName);

    @Update({"update ", TABLE_NAME, " set email=#{email},nick_name=#{nickName},city=#{city},sex=#{sex},sign=#{sign} where id=#{id}"})
    int updateProfile(User user);

    @Delete({"delete from ", TABLE_NAME, " where id=#{id}"})
    int deleteById(int id);

}
