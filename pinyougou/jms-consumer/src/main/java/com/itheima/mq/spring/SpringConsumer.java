package com.itheima.mq.spring;

import com.itheima.domain.User;

import javax.jms.*;
import java.io.Serializable;
import java.util.Map;

/**
 * 消息监听
 */
public class SpringConsumer implements MessageListener {

    /**
     * 接收消息
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        //接收文本消息
        if (message instanceof TextMessage){
            TextMessage textMessage=(TextMessage) message;

            try {
                System.out.println(textMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //接收MapMessage
        if (message instanceof MapMessage){
            //消息类型强转
            MapMessage mapMessage= (MapMessage) message;

            try {
                //获取数据
                Map<String,String> dataMap = (Map<String, String>) mapMessage.getObject("dataMap");
                //循环输出键值对数据
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    System.out.println(entry.getKey()+":"+entry.getValue());
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }

        }


        //接收ObjectMessage
        if (message instanceof ObjectMessage){
            //消息类型强转
            ObjectMessage objectMessage= (ObjectMessage) message;

            try {
                //获取数据,强转成JavaBean
                User user = (User) objectMessage.getObject();
                System.out.println(user);
            } catch (JMSException e) {
                e.printStackTrace();
            }

        }


    }
}
