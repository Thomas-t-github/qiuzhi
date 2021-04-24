package com.tyt.qiuzhi;


import com.tyt.qiuzhi.asyncmq.EventModel;
import com.tyt.qiuzhi.asyncmq.EventProducer;
import com.tyt.qiuzhi.asyncmq.EventType;
import com.tyt.qiuzhi.model.EntityType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitMQTest {

    @Autowired
    EventProducer eventProducer;

    @Test
    void testEventProducer(){

        /*eventProducer.fireEvent("message",new EventModel(EventType.FOLLOW)
                .setActorId(888).setEntityType(EntityType.ENTITY_USER)
                .setEntityId(666).setEntityOwnerId(666));*/

        eventProducer.fireEvent("search",new EventModel(EventType.FOLLOW)
                .setActorId(888).setEntityType(EntityType.ENTITY_USER)
                .setEntityId(666).setEntityOwnerId(666));


    }


}
