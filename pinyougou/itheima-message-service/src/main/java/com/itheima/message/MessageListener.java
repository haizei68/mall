package com.itheima.message;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageListener {

    @Autowired
    private  MessageSender messageSender;

    /**
     * 消息监听
     * @param dataMap
     */
    @JmsListener(destination = "itheima-message")
    public  void  getMapMessage(Map<String,String> dataMap) throws ClientException {

        //手机号
        String mobile = dataMap.get("mobile");
        //内容
        String param = dataMap.get("param");
        //签名
        String signName = dataMap.get("signName");
        //模板ID
        String templateCode = dataMap.get("templateCode");

        //集成阿里大于实现短信发送
        SendSmsResponse response = messageSender.sendSms(mobile, param, signName, templateCode);

        System.out.println("短信发送结果:"+response.getMessage());

    }
}
