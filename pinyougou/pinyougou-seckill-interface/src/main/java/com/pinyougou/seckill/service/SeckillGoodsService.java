package com.pinyougou.seckill.service;

import com.pinyougou.model.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    /**
     * 根据商品的ID去Redis中查询
     * @param id
     * @return
     */
    SeckillGoods getOne(Long id);

    /**
     * 查询所有秒杀商品
     * @return
     */
    List<SeckillGoods> list();
}
