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
public class SetTest {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 增加测试
     */
    @Test
    public void  testAdd(){
        redisTemplate.boundSetOps("Content").add("张三");
        redisTemplate.boundSetOps("Content").add("李四");
        redisTemplate.boundSetOps("Content").add("王五");
    }

    /**
     * 查询
     */
    @Test
    public void  testGet(){
        Set members = redisTemplate.boundSetOps("Content").members();
        System.out.println(members);
    }

    /**
     * 删除
     */
    @Test
    public void  testDelete(){
        redisTemplate.boundSetOps("Content").remove("李四");
    }


}
