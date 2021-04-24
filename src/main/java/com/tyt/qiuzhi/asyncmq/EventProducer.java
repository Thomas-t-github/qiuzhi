package com.tyt.qiuzhi.asyncmq;



import com.alibaba.fastjson.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public boolean fireEvent(String routingKey,EventModel eventModel){
        String eventModelString = JSONObject.toJSONString(eventModel);
        String exchangeName = "direct_qiuzhi_exchange";
        rabbitTemplate.convertAndSend(exchangeName,routingKey,eventModelString);
        return true;
    }

}
