package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.model.User;
import com.pinyougou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.DigestUtils;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination destination;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 发送验证码
     * @param phone
     */
    @Override
    public void createCode(String phone) throws JMSException {

        //产生验证码
        String code =String.valueOf((int)(Math.random()*10000));

        //验证码转JSON
        Map<String,String> dataMap=new HashMap<String, String>();
        dataMap.put("code",code);
        String jsonparam= JSON.toJSONString(dataMap);


        //先将验证码存入到Redis
        redisTemplate.boundHashOps("TelePhone").put(phone,code);


        sendMessage(phone, jsonparam);


    }

    @Override
    public Boolean checkCode(String phone, String code) {
        //从Redis中获取验证码, key:phone
        String rediscode= (String) redisTemplate.boundHashOps("TelePhone").get(phone);

        //校验验证码
        if (rediscode==null){
            return false;
        }

        //验证码不一致
        if (!code.equals(rediscode)){
            return false;
        }

        return true;
    }

    /**
     * 发送验证码
     * @param phone
     * @param jsonparam
     * @throws JMSException
     */
    public void sendMessage(final String phone, final String jsonparam) throws JMSException {
        //向ActiveMQ发送短信内容
        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //MapMessage
                MapMessage mapMessage = session.createMapMessage();

                //手机号
                mapMessage.setString("mobile",phone);

                //内容
                mapMessage.setString("param",jsonparam);

                //签名
                mapMessage.setString("signName","学java看这里");

                //模板
                mapMessage.setString("templateCode","SMS_145592557");

                return mapMessage;
            }
        });
    }


    /***
     * 增加User信息
     * @param user
     * @return
     */
    @Override
    public int add(User user) {
        //当前时间
        Date now = new Date();

        //设置注册时间
        user.setCreated(now);
        user.setUpdated(now);

        //设置密码
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        int count = userMapper.insertSelective(user);

        if (count>0){
            redisTemplate.boundHashOps("TelePhone").delete(user.getPhone());
        }

        return count ;
    }

    /**
     * 验证用户名是否被张勇
     * @param username
     * @return
     */
    @Override
    public User getUserByName(String username) {
        User user = new User();
        user.setUsername(username);

        return userMapper.selectOne(user);
    }

}
