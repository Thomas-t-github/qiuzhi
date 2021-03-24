package com.tyt.qiuzhi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Advertisement {

    private String title;               //标题
    private Date startDate;             //开始时间
    private Date endDate;               //结束时间
    private String siteLink;            //网站链接
    private List<String> imageUrl;      //轮播图片链接

}
