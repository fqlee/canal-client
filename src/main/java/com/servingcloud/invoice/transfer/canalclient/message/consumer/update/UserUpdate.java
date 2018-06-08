package com.servingcloud.invoice.transfer.canalclient.message.consumer.update;

import com.alibaba.fastjson.JSON;
import com.servingcloud.invoice.transfer.canalclient.entity.User;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author fqlee
 * @since 2018/6/8
 */
@RabbitListener(queues = "DATABASE.USER.UPDATE")
public class UserUpdate {

    @Autowired
    private MongoTemplate mongoTemplate;

    @RabbitHandler
    public void process(String message){
        User user = JSON.parseObject(message,User.class);
        mongoTemplate.remove(user);
    }
}
