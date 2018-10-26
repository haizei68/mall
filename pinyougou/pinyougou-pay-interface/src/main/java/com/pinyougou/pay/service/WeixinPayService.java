package com.pinyougou.pay.service;

import java.util.Map;

public interface WeixinPayService {

    /**
     * 创建二维码,查询二维码支付地址
     * out_trade_no     交易单号
     * total_fee        交易总金额
     */
    Map createNative(String out_trade_no,String total_fee);

    /**
     * 查询支付状态
     */
    Map queryPayStatus(String out_trade_no);


    /**
     * 关闭订单
     * @param tradeoutno
     * @return
     */
    Map<String,String> closePay(String tradeoutno);
}
