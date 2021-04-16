package com.tyt.qiuzhi.elasticsearch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(indexName = "qiuzhi",type = "question")
public class SearchEntity {

    @Id
    private Integer id;     //问题ID

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;       //标题

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String description;     //描述

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String label;       //标签

    @Field(type = FieldType.Integer)
    private Integer reward;         //悬赏

    @Field(type = FieldType.Integer)
    private Integer userId;         //提问人ID

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String nickName;       //提问人昵称

    @Field(type = FieldType.Keyword)
    private String headUrl;       //提问人头像

    @Field(type = FieldType.Date)
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;       //提问时间

    @Field(type = FieldType.Integer)
    private Integer commentCount;       //评论数

}
