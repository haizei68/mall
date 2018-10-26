package com.itheima.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value="/msg")
public class SendMapMessageController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 短信发送测试
     * @param code
     * @return
     */
    @RequestMapping(value="/send")
    public String sendMsg(String code){
        //要发送的数据
        Map<String,String> dataMap=new HashMap<String, String>();

        //手机号
        dataMap.put("mobile","18665407449");
        //内容
        dataMap.put("param","{\"code\":\""+code+"\"}");
        //签名
        dataMap.put("signName","学java看这里");
        //模板ID
        dataMap.put("templateCode","SMS_145592557");

        //向MQ发送消息
        jmsMessagingTemplate.convertAndSend("itheima-message",dataMap);
        return "success";
    }

}
