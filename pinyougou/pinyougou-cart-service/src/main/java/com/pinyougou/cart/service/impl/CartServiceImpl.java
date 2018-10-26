package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.model.Cart;
import com.pinyougou.model.Item;
import com.pinyougou.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemMapper itemMapper;

    /**
     * 合并方法
     * 从Redis中查询的用户购物车
     * @param rediscarts
     * @param cookiecarts
     * @return
     */
    @Override
    public List<Cart> megerCart(List<Cart> rediscarts, List<Cart> cookiecarts) {

        //循环Cookie中取的购物车数据  cookiecarts
        for (Cart cookiecart : cookiecarts) {
            //循环当前购物车明细
            for (OrderItem orderItem : cookiecart.getOrderItemList()) {
                Long itemid = orderItem.getItemId();    //商品ID
                Integer num = orderItem.getNum();       //购买数量

                //将所有Cookie中加入购物车=商品转移到Redis购物车集合中
                rediscarts = add(rediscarts, itemid, num);
            }
        }
        return rediscarts;
    }

    /**
     * 加入购物车
     * @param carts
     * @param itemid
     * @param num
     * @return
     */
    @Override
    public List<Cart> add(List<Cart> carts, Long itemid, Integer num) {

        //防止空指针
        if (carts == null) {
            carts = new ArrayList<Cart>();
        }

        //查询Item
        Item item = itemMapper.selectByPrimaryKey(itemid);

        //判断该商家的购物车数据是否已经存在
        Cart cart = searchSellerCart(carts, item.getSellerId());
        //如果存在,则获取该商家对应的Cart对象
        if (cart != null) {
            //当前需要购买的商品,购物车是否已经存在
            OrderItem orderItem = searchOrderItem(itemid, cart.getOrderItemList());

            //如果已经存在,则让购买数量增加+运算总价格
            if (orderItem != null) {
                //数量增加 [减掉购物车数据,是否小于0]
                orderItem.setNum(orderItem.getNum() + num);
                //总价格=单价*总数量
                double totalFee = (orderItem.getPrice().doubleValue()) * (orderItem.getNum().intValue());
                orderItem.setTotalFee(new BigDecimal(totalFee));

                //如果当前商品明细购买数量<=0,则从购物车中移除该商品明细
                if (orderItem.getNum() <= 0) {
                    cart.getOrderItemList().remove(orderItem);
                }

                if (cart.getOrderItemList().size() <= 0) {
                    carts.remove(cart);
                }


            } else {
                //不存在,则创建新的购物车明细OrderItem
                orderItem = createOrderItem(num, item);

                //将orderItem加入集合中
                createOrderItemList(cart, orderItem);
            }

        } else {
            //如果该商家对应的Cart不存在,创建购物车新的Cart
            cart = new Cart();
            cart.setSellerName(item.getSeller());
            cart.setSellerId(item.getSellerId());

            //创建一个新的OrderItem
            OrderItem orderItem = createOrderItem(num, item);
            //添加购物车的明细集合信息
            createOrderItemList(cart, orderItem);
            //将新的cart对象加入到集合中
            carts.add(cart);
        }

        return carts;
    }


    /**
     * 添加购物车的明细集合信息
     *
     * @param cart
     * @param orderItem
     */
    public void createOrderItemList(Cart cart, OrderItem orderItem) {
        //将新的购物车明细加入到Cart 的orderItemList属性
        cart.getOrderItemList().add(orderItem);
    }

    /**
     * 创建OrderItem对象
     *
     * @param num
     * @param item
     * @return
     */
    public OrderItem createOrderItem(Integer num, Item item) {
        OrderItem orderItem;
        orderItem = new OrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setPicPath(item.getImage());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        //价格/计算=数量*单价
        orderItem.setTotalFee(new BigDecimal(num * (item.getPrice()).doubleValue()));
        return orderItem;
    }

    /**
     * 从商家购物车名字中搜索当前商品的明细信息
     *
     * @param itemid
     * @param
     */
    public OrderItem searchOrderItem(Long itemid, List<OrderItem> orderItems) {
        //当前需要购买的商品,购物车是否已经存在
        for (OrderItem orderItem : orderItems) {
            //商品ID相等,说明商品明细已经存在
            if (orderItem.getItemId().longValue() == itemid.longValue()) {
                return orderItem;
            }
        }

        return null;
    }

    /**
     * 获取商家的Cart信息
     *
     * @param carts
     * @param sellerId
     * @return
     */
    public Cart searchSellerCart(List<Cart> carts, String sellerId) {
        for (Cart cart : carts) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户登录,购物车数据存入到Redis
     *
     * @param username
     * @param carts
     */
    @Override
    public void addGoodsToRedis(String username, List<Cart> carts) {
        redisTemplate.boundHashOps("CartListInfo").put(username, carts);
    }

    @Override
    public List<Cart> findGoodsFromRedis(String username) {
        return (List<Cart>) redisTemplate.boundHashOps("CartListInfo").get(username);
    }

}
