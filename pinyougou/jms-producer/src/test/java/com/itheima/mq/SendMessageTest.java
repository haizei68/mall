package com.itheima.mq;

import com.itheima.domain.User;
import com.itheima.mq.spring.SpringProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息发送测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-mq.xml")
public class SendMessageTest {
    @Autowired
    private SpringProducer springQueueProducer;

    /**
     * 发送文本消息测试
     */
    @Test
    public void testSendTextMessage(){
        String text="小红你吃饭了!";
        springQueueProducer.sendTextMessage(text);
    }

    /**
     * 发送Map消息测试
     */
    @Test
    public void testSendMapMessage(){
        Map<String,String> dataMap=new HashMap<String, String>();

        dataMap.put("userName","小红");
        dataMap.put("age","22");
        dataMap.put("address","深圳廷尉");

        springQueueProducer.sendMapMessage(dataMap);
    }


    /**
     * 发送Map消息测试
     */
    @Test
    public void testSendObjectMessage(){
        User user = new User(22,"小明",new Date());

        springQueueProducer.sendObjectMessage(user);
    }

}
