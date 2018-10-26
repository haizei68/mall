package com.pinyougou.order.service;

import com.pinyougou.model.Order;
import com.pinyougou.model.PayLog;

public interface OrderService {

    /**
     * 根据用户名获取支付日志
     * @return
     */
    PayLog getPayLogByUserId(String username);


    /**
     * 创建订单
     */
    int add(Order order);

    /**
     * 更改订单和支付日志状态
     * @param username
     */
    void updatePayStatus(String username,String transaction_id);
}
