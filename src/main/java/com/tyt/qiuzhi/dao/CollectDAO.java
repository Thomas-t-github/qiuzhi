package com.tyt.qiuzhi.dao;

import com.tyt.qiuzhi.model.Collect;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CollectDAO {

    String TABLE_NAME = " collect ";
    String INSERT_FIELDS = " user_id, entity_id, entity_type, created_date, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{userId},#{entityId},#{entityType},#{createdDate},#{status})"})
    int addCollect(Collect collect);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where entity_id=#{entityId} and entity_type=#{entityType} order by id desc"})
    List<Collect> selectByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where user_id=#{userId} and status=0 order by id desc limit 0,10"})
    List<Collect> selectByUserId(int userId);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where user_id=#{userId} and status=0 and entity_type=#{entityType} and entity_id=#{entityId}"})
    List<Collect> getUserCollectStatus(@Param("userId") int userId,@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Collect selectById(int id);

    @Update({"update ", TABLE_NAME, " set status=#{status} where id=#{id}"})
    int updateStatusById(@Param("id") int id, @Param("status") int status);

    @Update({"update ", TABLE_NAME, " set status=#{status} where entity_id=#{entityId} and entity_type=#{entityType}"})
    int updateStatus(@Param("entityId") int entityId, @Param("entityType") int entityType, @Param("status") int status);


    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCollectsCount(int userId);

}
