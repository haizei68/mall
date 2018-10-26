package com.pinyougou.manager.service;

import com.pinyougou.mq.MessageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * 消息发送对象
 */
@Component
public class MessageSender {

    //JmsTemplate
    @Autowired
    private JmsTemplate jmsTemplate;

    //指定发送地址
    @Autowired
    private Destination destination;

    /**
     * 需要向ActiveMQ发送ObjectMessage消息类型
     *
     */
    public void sendObjectMessage(final MessageInfo messageInfo){
        jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                //创建一个ObjectMessage
                ObjectMessage objectMessage = session.createObjectMessage();
                //设置消息内容
                objectMessage.setObject(messageInfo);
                return objectMessage;
            }
        });
    }
}
