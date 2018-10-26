package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.model.Cart;
import com.pinyougou.model.Order;
import com.pinyougou.model.OrderItem;
import com.pinyougou.model.PayLog;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    /**
     * 创建订单
     *
     * @param order
     * @return
     */
    @Override
    public int add(Order order) {
        //查询购物车
        List<Cart> carts = (List<Cart>) redisTemplate.boundHashOps("CartListInfo").get(order.getUserId());

        //返回的数据
        int account = 0;

        //订单总金额
        double money = 0;

        List<String> idlist = new ArrayList<String>();

        for (Cart cart : carts) {
            //生成一个订单编号
            long id = idWorker.nextId();

            //订单创建
            Order newOrder = new Order();
            newOrder.setOrderId(id);
            newOrder.setCreateTime(order.getCreateTime());              //创建时间
            newOrder.setUpdateTime(order.getUpdateTime());              //修改时间
            newOrder.setReceiver(order.getReceiver());                  //收件人
            newOrder.setReceiverAreaName(order.getReceiverAreaName());  //收件地址
            newOrder.setReceiverMobile(order.getReceiverMobile());      //收件人联系电话
            newOrder.setStatus(order.getStatus());                      //状态
            newOrder.setPaymentType(order.getPaymentType());            //支付方式
            newOrder.setUserId(order.getUserId());                      //买家ID
            newOrder.setSourceType("2");                                //2:PC端
            newOrder.setSellerId(cart.getSellerId());                   //商家ID

            //支付总金额
            double totalFee = 0;

            //循环商品明细
            for (OrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());

                //订单ID
                orderItem.setOrderId(id);

                //总金额计算
                totalFee += orderItem.getTotalFee().doubleValue();

                //增加商品明细
                account += orderItemMapper.insertSelective(orderItem);
            }

            //设置总金额
            newOrder.setPayment(new BigDecimal(totalFee));

            //增加订单信息
            account += orderMapper.insertSelective(newOrder);

            //订单总金额计算
            money += totalFee;
            idlist.add(id + "");

        }

        //清空购物车
        redisTemplate.boundHashOps("CartListInfo").delete(order.getUserId());

        //微信支付
        if (order.getPaymentType().equals("1")) {
            //支付日志记录
            PayLog payLog = new PayLog();
            payLog.setOutTradeNo(idWorker.nextId() + "");
            payLog.setCreateTime(order.getCreateTime());
            payLog.setTotalFee((long) (money));     //支付金额
            payLog.setUserId(order.getUserId());
            payLog.setTradeState("0");
            payLog.setPayType("1");

            //将订单号存入日志中
            String ids = idlist.toString().replace("[", "").replace("]", "").replace(" ", "");
            payLog.setOrderList(ids);

            //将支付日志加入到数据
            payLogMapper.insertSelective(payLog);

            //将支付日志添加到缓存
            redisTemplate.boundHashOps("PayLog").put(order.getUserId(), payLog);
        }

        return account;
    }

    /**
     * 更改订单状态和日志支付状态
     *
     * @param username
     */
    @Override
    public void updatePayStatus(String username, String transaction_id) {
        //修改日志支付状态
        PayLog payLog = (PayLog) redisTemplate.boundHashOps("PayLog").get(username);

        if (payLog != null) {
            //修改日志信息
            payLog.setTransactionId(transaction_id);
            payLog.setTradeState("1");
            payLog.setPayTime(new Date());      //支付时间,这里时间不对

            //修改订单状态
            String orderList = payLog.getOrderList();
            String[] split = orderList.split(",");
            if (split != null) {

                for (String orderid : split) {

                    //根据id修改订单状态
                    Order order = new Order();
                    order.setOrderId(Long.parseLong(orderid));
                    order.setStatus("2");           //状态
                    order.setPaymentTime(payLog.getPayTime());  //支付时间

                    //执行修改
                    orderMapper.updateByPrimaryKeySelective(order);
                }
            }

            //修改日志状态
            payLogMapper.updateByPrimaryKeySelective(payLog);

            //清除Redis中的缓存
            redisTemplate.boundHashOps("PayLog").delete(username);
        }

    }

    /***
     * 获取支付日志
     * @param username
     * @return
     */
    @Override
    public PayLog getPayLogByUserId(String username) {

        return (PayLog) redisTemplate.boundHashOps("PayLog").get(username);
    }
}
