package com.pinyougou.task;

import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.model.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Set;

/***
 *
 * @Author:shenkunlin
 * @Description:itheima
 * @date: 2018/9/28 9:09
 *
 ****/
@Service
public class SeckillGoodsTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /*****
     * 将数据库中秒杀商品查询出来，并加入到Redis缓存
     * 每30秒执行一次
     */
    @Scheduled(cron = "0/15 * * * * ?")
    public void pushSeckillGoods2Redis(){
        System.out.println("-------------------");

        /****
         * 查询出秒杀商品
         *      1)已审核
         *      2)库存数量>0
         *      3)秒杀时间限制    活动开始时间=< 当前时间 <=活动结束时间
         *      4)排除之前已经加入到Redis中的秒杀商品   1,2
         *
         *      SQL:SELECT * FROM tb_seckill_goods WHERE
         *                          status='1'                              状态
         *                          stock_count>0                           库存数量>0
         *                          NOW() between start_time AND end_time   秒杀活动时间
         *                          AND id NOT IN(1,2)
         */
        Example example = new Example(SeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();

        //审核状态
        criteria.andEqualTo("status","1");

        //商品库存数量>0
        criteria.andGreaterThan("stockCount",0);

        //活动时间   活动开始时间=< 当前时间 <=活动结束时间
        criteria.andCondition("NOW() BETWEEN start_time AND end_time");

        //排除之前已经加入到Redis中的秒杀商品
        Set<Long> ids = redisTemplate.boundHashOps("SeckillGoods").keys();
        if(ids!=null && ids.size()>0){
            criteria.andNotIn("id",ids);
        }

        //查询商品
        List<SeckillGoods> goods = seckillGoodsMapper.selectByExample(example);

        if(goods==null || goods.size()<=0){
            System.out.println("没有可以加入到Redis缓存的数据");
        }

        //循环将每个秒杀商品存入到Redis缓存
        for (SeckillGoods good : goods) {
            redisTemplate.boundHashOps("SeckillGoods").put(good.getId(),good);

            //将商品个数存入队列中
            pushQueue(good);
        }
    }

    public void pushQueue(SeckillGoods good){
        for (int i=0;i<good.getStockCount();i++){
            redisTemplate.boundListOps("SeckilGoods_Id_"+good.getId()).leftPush(good.getId());
        }
    }

}
