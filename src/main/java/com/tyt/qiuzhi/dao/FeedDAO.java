package com.tyt.qiuzhi.dao;

import com.tyt.qiuzhi.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FeedDAO {


    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " type, user_id, created_date, data ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{type},#{userId},#{createdDate},#{data})"})
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int addFeed(Feed feed);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Feed selectFeedById(int id);

    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);


}
