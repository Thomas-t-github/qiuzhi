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
public class Question {

    private int id;
    private String title;       //标题
    private String description;     //描述
    private String label;       //标签
    private int reward;         //悬赏
    private int userId;         //提问人
    private Date createdDate;       //创建时间
    private int commentCount;       //评论数
}
