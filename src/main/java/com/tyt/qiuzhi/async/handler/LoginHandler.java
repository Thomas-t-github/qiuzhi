package com.tyt.qiuzhi.async.handler;

import com.tyt.qiuzhi.async.EventHandler;
import com.tyt.qiuzhi.async.EventModel;
import com.tyt.qiuzhi.async.EventType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class LoginHandler implements EventHandler {


    @Override
    public void doHandle(EventModel eventModel) {
        System.out.println(eventModel.getExt("email")+"登录成功！！！");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
