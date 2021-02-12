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
public class Collect {
    private int id;             //收藏id
    private int userId;         //收藏人
    private int entityId;       //实体id
    private int entityType;     //实体类型
    private Date createdDate;   //创建时间
    private int status;         //收藏状态
}
