package com.itheima.controller;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 消费者
 */
@Component
public class Consumer {
    @JmsListener(destination = "itcast")
    public void readMessage(String text){
        System.out.println("读到的消息:"+text);
    }

    @JmsListener(destination = "heima_map")
    public void readMap(Map map){
        System.out.println(map);
    }
}
