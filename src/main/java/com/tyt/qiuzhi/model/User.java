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
public class User {

    private int id;     //id
    private String email;       //邮箱
    private String password;       //密码
    private String nickName;       //昵称
    private String salt;           //盐
    private String city;           //城市
    private int sex;           //性别
    private String sign;           //签名
    private Date createdDate;       //创建时间
    private String headUrl;        //头像URL

}
