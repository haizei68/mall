package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.model.SeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据商品的ID去Redis中查询
     * @param id
     * @return
     */
    @Override
    public SeckillGoods getOne(Long id) {
        return (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods").get(id);
    }

    /**
     * 查询所有秒杀商品
     * @return
     */
    @Override
    public List<SeckillGoods> list() {
        return redisTemplate.boundHashOps("SeckillGoods").values();
    }
}
