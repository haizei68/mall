package com.itheima.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-redis.xml")
public class ValueTest {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 增加测试
     */
    @Test
    public void  testAdd(){
        redisTemplate.boundValueOps("name3").set("itheima3");
    }

    @Test
    public void  testGet(){
        String  result=(String) redisTemplate.boundValueOps("name").get();

        System.out.println(result);
    }

    @Test
    public void  testDelete(){
        redisTemplate.delete("name");
    }


}
