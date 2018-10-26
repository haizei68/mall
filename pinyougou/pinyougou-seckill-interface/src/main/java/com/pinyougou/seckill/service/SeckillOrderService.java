package com.pinyougou.seckill.service;

import com.pinyougou.model.SeckillOrder;

public interface SeckillOrderService {
    /**
     * 下单操作
     * @param username
     */
    void add(String username,Long id);

    /**
     * 根据用户名查询用户的订单信息
     * @param username
     * @return
     */
    SeckillOrder getOrderByUserName(String  username);

    /**
     * 修改订单状态
     * @param username
     * @param transaction_id
     */
    void updatePayStatus(String username, String transaction_id);

    /**
     * 移除订单
     * @param username
     */
    void removeOrder(String username);
}
