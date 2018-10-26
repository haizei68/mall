package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.model.PayLog;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
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
    private OrderService orderService;

    @RequestMapping(value = "/queryPayStatus")
    public Result queryPayStatus(String tradeoutno) throws InterruptedException {

        int count=0;

        while (true){
            //支付状态信息
            Map map = weixinPayService.queryPayStatus(tradeoutno);

            //支付异常
            if (map==null){
                return new Result(false,"支付异常!");
            }

            //判断支付状态
            if (map.get("trade_state").equals("SUCCESS")){
                //获取用户登录名
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
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
                return new Result(false,"timeout");
            }
        }
    }


    /**
     * 创建2维码
     * @return
     */
    @RequestMapping(value = "createNative")
    public Map createNative(){
        //String  trade_out_no="9896332344"+(int)(Math.random ()*10000);
        String username=SecurityContextHolder.getContext().getAuthentication().getName();

        PayLog payLog = orderService.getPayLogByUserId(username);

        //获取二维码支付地址信息
        //Map dataMap=weixinPayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");    //单位:分
        Map dataMap=weixinPayService.createNative(payLog.getOutTradeNo(),"1");    //单位:分

        return dataMap;
    }
}
