package com.itheima.mq.topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 订阅模式消息生产者
 */
public class DemoTopicProducer {

    //消息生产者 ActiveMQ
    public static void main(String[] args) throws JMSException {

        //创建连接工厂对象ConnectionFactory
        ConnectionFactory connectionFactory =new ActiveMQConnectionFactory("tcp://192.168.211.128:61616");
        //创建链接对象Connection
        Connection connection = connectionFactory.createConnection();

        //打开链接
        connection.start();

        //创建会话对象Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //创建消息   相当于表中插入一条数据
        TextMessage textMessage = session.createTextMessage();
        textMessage.setText("小红你来了");


        Topic topic =session.createTopic("topic_test");

        //创建消息发送者
        MessageProducer producer=session.createProducer(topic);

        //消息发送
        producer.send(textMessage);

        //关闭资源
        session.close();
        connection.close();
    }
}
