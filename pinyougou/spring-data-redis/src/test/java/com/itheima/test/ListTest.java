package com.itheima.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-redis.xml")
public class ListTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testLeftAdd(){
        redisTemplate.boundListOps("leftName").leftPush("张三1");
        redisTemplate.boundListOps("leftName").leftPush("张三2");
        redisTemplate.boundListOps("leftName").leftPush("张三3");
    }

    @Test
    public void testLeftGet(){
        Object leftName = redisTemplate.boundListOps("leftName").leftPop();
        System.out.println(leftName);
    }
}
