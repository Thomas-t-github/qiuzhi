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
public class LoginTicket {

    private int id;         //id
    private int userId;     //用户id
    private String ticket;      //ticket票
    private Date expired;       //有效时间
    private int status;        //状态 0有效 1无效

}
