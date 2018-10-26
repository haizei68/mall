package com.itheima.mq.spring;

import com.itheima.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.Map;

/**
 * 点对点消息发送
 */
@Component
public class SpringProducer {

    //JmsTemplate
    @Autowired
    private JmsTemplate jmsTemplate;

    //目标地址
    @Autowired
    private Destination destination;

    public  void  sendTextMessage(final String text){
        jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                //创建一个文本消息
                TextMessage textMessage = session.createTextMessage();
                textMessage.setText(text);
                return textMessage;
            }
        });
    }

    public void sendMapMessage(final Map<String,String>dataMap){
        jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                //创建Map消息类型
                MapMessage mapMessage = session.createMapMessage();

                //将Map对象存入
                mapMessage.setObject("dataMap",dataMap);
                return mapMessage;
            }
        });
    }

    public void sendObjectMessage(final User user){
        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //创建一个ObejectMessage对象
                ObjectMessage objectMessage = session.createObjectMessage();

                //设置要发送的消息对象
                objectMessage.setObject(user);
                return objectMessage;
            }
        });
    }

}
