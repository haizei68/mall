package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.model.SeckillOrder;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private SeckillOrderService orderService;

    @RequestMapping(value = "/queryPayStatus")
    public Result queryPayStatus(String tradeoutno) throws InterruptedException {

        int count=0;

        //获取用户登录名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        while (true){
            //支付状态信息
            Map map = weixinPayService.queryPayStatus(tradeoutno);

            //支付异常
            if (map==null){
                return new Result(false,"支付异常!");
            }

            //判断支付状态
            if (map.get("trade_state").equals("SUCCESS")){

                //支付成功,更改支付状态和订单状态  支付记录---order
                orderService.updatePayStatus(username, (String) map.get("transaction_id"));


                return new Result(true,"支付成功!");
            }

            //支付失败FAIL
            if (map.get("trade_state").equals("PAYERROR")){
                return new Result(true,"支付失败!");
            }

            //每3秒查询一次
            Thread.sleep(3000);

            //计算器
            count++;
            //超时设置
            if (count>5){
                //如果用户订单30秒后检测到仍然未支付,则关闭该订单
                Map<String,String> closeResult=weixinPayService.closePay(tradeoutno);

                System.out.println(closeResult);

                //关闭成功
                if (closeResult.get("result_code").equals("SUCCESS")){
                    //移除Redis中的订单信息,让库存增加
                    orderService.removeOrder(username);
                }else {
                    //关闭失败-----(有可能已支付)
                    if(closeResult.get("err_code").equals("ORDERPAID")){
                        //查询支付信息
                        map = weixinPayService.queryPayStatus(tradeoutno);

                        //支付成功,更改支付状态和订单状态  支付记录----OrderList
                        orderService.updatePayStatus(username, (String) map.get("transaction_id"));
                    }
                }

                //移出Redis中的订单信息,让库存增加

                return new Result(false,"timeout");
            }
        }
    }


    /**
     * 创建2维码
     * @return
     */
    @RequestMapping(value = "/createNative")
    public Map createNative(){
        //用户名
        String username=SecurityContextHolder.getContext().getAuthentication().getName();

        SeckillOrder seckillOrder = orderService.getOrderByUserName(username);

        //获取二维码支付地址信息
        //Map dataMap=weixinPayService.createNative(seckillOrder.getId().toString(),seckillOrder.getMoney()+"");    //单位:分
        Map dataMap=weixinPayService.createNative(seckillOrder.getId().toString(),"1");    //单位:分

        return dataMap;
    }
}
