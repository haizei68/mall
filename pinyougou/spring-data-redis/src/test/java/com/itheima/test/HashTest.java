package com.itheima.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-redis.xml")
public class HashTest {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 增加数据测试
     */
    @Test
    public void testSetValue(){
        redisTemplate.boundHashOps("namehash").put("a", "唐僧");
        redisTemplate.boundHashOps("namehash").put("b", "悟空");
        redisTemplate.boundHashOps("namehash").put("c", "八戒");
        redisTemplate.boundHashOps("namehash").put("d", "沙僧");
    }

    /**
     * 提取所有key
     */
    @Test
    public void testGetkeys(){
        Set s = redisTemplate.boundHashOps("namehash").keys();
        System.out.println(s);
    }

    /**
     * 提取所有值
     */
    @Test
    public void testGetValues(){
        List values = redisTemplate.boundHashOps("namehash").values();
        System.out.println(values);
    }

    /**
     * 根据key提取值
     */
    @Test
    public void testGetValueByKey(){
        Object o = redisTemplate.boundHashOps("namehash").get("b");
        System.out.println(o);
    }

    /**
     * 根据key移除值
     */
    @Test
    public void testRemoveValueByKey(){
         redisTemplate.boundHashOps("namehash").delete("c");

    }


}
