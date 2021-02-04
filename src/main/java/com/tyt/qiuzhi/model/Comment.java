package com.tyt.qiuzhi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Comment {

    private int id;             //评论id
    private String content;     //评论内容
    private int userId;         //评论人
    private int entityId;       //实体id
    private int entityType;     //实体类型
    private Date createdDate;       //创建时间
    private int status;         //评论状态

}
