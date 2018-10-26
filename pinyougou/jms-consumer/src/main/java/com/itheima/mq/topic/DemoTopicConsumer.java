package com.itheima.mq.topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.IOException;

/**
 * 消费方
 */
public class DemoTopicConsumer {

    public static void main(String[] args) throws JMSException, InterruptedException, IOException {
        //创建链接工厂对象ConnectionFactory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.211.128:61616");
        //创建链接对象Connection
         Connection connection = connectionFactory.createConnection();

        //开启连接
        connection.start();

        //创建Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //创建消息读取目标地址
        Topic queue = session.createTopic("topic_test");

        //创建消息读取对象
        MessageConsumer consumer = session.createConsumer(queue);

        //创建监听器接收消息, 属于一个线程
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof  TextMessage){
                    //消息类型强转
                    TextMessage textMessage=(TextMessage) message;
                    try {
                        //输出消息
                        System.out.println("订阅消息接收:"+textMessage.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
      //键盘录入,防止主线程结束
        System.in.read();


        //关闭资源
        session.close();
        connection.close();

    }



    //消费方
    public static void main1(String[] args) throws JMSException {
        //创建链接工厂对象ConnectionFactory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.211.128:61616");
        //创建链接对象Connection
        Connection connection = connectionFactory.createConnection();

        //开启连接
        connection.start();

        //创建Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //创建消息读取目标地址
        Topic queue = session.createTopic("topic_test");

        //创建消息读取对象
        MessageConsumer consumer = session.createConsumer(queue);

        while (true) {
            //循环接收数据
            Message message = consumer.receive(10000);
            //判断消息是否
            if (message != null) {
                //文本消息
                if (message instanceof TextMessage) {
                    //强壮成文本消息类型
                    TextMessage textMessage = (TextMessage) message;
                    System.out.println("接收到的消息是:" + textMessage.getText());
                }
                break;
            }
        }

        //关闭资源
        session.close();
        connection.close();

    }
}
