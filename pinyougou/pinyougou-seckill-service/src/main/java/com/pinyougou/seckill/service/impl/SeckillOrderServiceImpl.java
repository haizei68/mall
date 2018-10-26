package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.model.SeckillGoods;
import com.pinyougou.model.SeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    /**
     * 下订单
     * @param username
     * @param id
     */
    @Override
    public void add(String username, Long id) {

        //先从队列取
        Object queueId=redisTemplate.boundListOps("SeckilGoods_Id_"+id).rightPop();

        if (queueId==null){
            throw new RuntimeException("已售罄");
        }

        SeckillGoods good = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods").get(id);

        /*
        //有库存,则添加订单
        if (good==null||good.getStockCount()<=0){
            throw  new  RuntimeException("已售罄");
        }*/

            //创建订单
            SeckillOrder seckillOrder=new SeckillOrder();
            seckillOrder.setId(idWorker.nextId());
            seckillOrder.setSeckillId(id);
            seckillOrder.setMoney(good.getCostPrice());
            seckillOrder.setUserId(username);
            seckillOrder.setSellerId(good.getSellerId());
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setStatus("0");        //未付款

            //将用户的订单信息存入到Redis中
            redisTemplate.boundHashOps("SeckillOrder").put(username,seckillOrder);

            //库存减一
            good.setStockCount(good.getStockCount()-1);

            if (good.getStockCount()<=0){
                //将该商品的秒杀信息同步到数据中
                seckillGoodsMapper.updateByPrimaryKeySelective(good);
                //如果商品已经卖完,则清空redis中该商品
                redisTemplate.boundHashOps("SeckillGoods").delete(id);
            }else {
                //修改Redis缓存
                redisTemplate.boundHashOps("SeckillGoods").put(id,good);
            }
    }


    /**
     * 根据用户名查询用户的订单信息
     * @param username
     * @return
     */
    @Override
    public SeckillOrder getOrderByUserName(String username) {
        return (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
    }

    /**
     * 修改订单状态
     * @param username
     * @param transaction_id
     */
    @Override
    public void updatePayStatus(String username, String transaction_id) {
        //获取订单
        SeckillOrder order= (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);

        if (order!=null){
            //修改数据
            order.setPayTime(new Date());
            order.setStatus("1");
            order.setTransactionId(transaction_id);

            //将订单存入MySQL中
            seckillOrderMapper.insertSelective(order);
            //清空缓存
            redisTemplate.boundHashOps("SeckillOrder").delete(username);

        }

    }

    /**
     * 移除订单
     * @param username
     */
    @Override
    public void removeOrder(String username) {
        //查询出订单
        SeckillOrder order= (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);

        if (order!=null){
            //查询商品
            SeckillGoods good= (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods").get(order.getSeckillId());
            if(good==null){
                good=seckillGoodsMapper.selectByPrimaryKey(order.getSeckillId());
            }

            //移除订单
            redisTemplate.boundHashOps("SeckillOrder").delete(username);

            //商品数量增加
            good.setStockCount(good.getStockCount()+1);
            //修改Redis
            redisTemplate.boundHashOps("SeckillGoods").put(good.getId(),good);

            //队列+1
            redisTemplate.boundListOps("SeckilGoods_Id_"+good.getId()).leftPush(good.getId());
        }
    }
}
