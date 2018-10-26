package com.itheima.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 *生产者
 */
@RestController
@RequestMapping(value = "/queue")
public class QueueController {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/send")
    public String  send(String  text){
        jmsMessagingTemplate.convertAndSend("itcast",text);

        return "ok";
    }

    @RequestMapping("/sendmap")
    public String sendMap(){
         Map<String,String> map = new HashMap<String,String>();
         map.put("mobile","123456");
         map.put("content","恭喜获得1000快");
         jmsMessagingTemplate.convertAndSend("heima_map",map);

         return "发送OK";
    }

}
