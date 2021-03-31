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
public class Oauth {

    private int id;
    private int userId;
    private String openId;
    private int appType;
    private Date createdDate;

}
