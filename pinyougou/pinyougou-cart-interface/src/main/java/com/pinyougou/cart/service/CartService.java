package com.pinyougou.cart.service;

import com.pinyougou.model.Cart;

import java.util.List;

public interface CartService {

    /**
     * 加入购物车
     * @param carts
     * @param itemid
     * @param num
     * @return
     */
    List<Cart> add(List<Cart>carts,Long itemid,Integer num);

    /**
     * 增加购物车数据
     * @param
     * @param
     */
    void addGoodsToRedis(String username,List<Cart> carts);

    /**
     * 查询某个用户的购物车数据
     * @param username
     * @return
     */
    List<Cart> findGoodsFromRedis(String username);

    /**
     * 合并方法
     * @param rediscarts
     * @param cookiecarts
     * @return
     */
    List<Cart> megerCart(List<Cart> rediscarts, List<Cart> cookiecarts);

}
