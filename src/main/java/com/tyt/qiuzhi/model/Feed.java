package com.tyt.qiuzhi.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Feed {

    private int id;             //事件ID
    private int type;           //事件类型
    private int userId;             //事件的主角
    private Date createdDate;       //事件产生时间
    private String data;            //事假携带的数据
    private JSONObject dataJSON = null;         //方便渲染引擎获取数据

    public String get(String key) {
        return dataJSON == null ? null : dataJSON.getString(key);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        dataJSON = JSONObject.parseObject(data);
    }
}
