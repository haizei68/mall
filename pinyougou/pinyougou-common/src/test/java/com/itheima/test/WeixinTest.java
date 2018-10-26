package com.itheima.test;

import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.util.HttpClient;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class WeixinTest {
    /**
     * 订单状态查询
     */
    public static void queryPayStatus() throws Exception {
        Map dataMap = new HashMap();
        dataMap.put("appid", "wx8397f8696b538317");
        dataMap.put("mch_id", "1473426802");
        dataMap.put("out_trade_no", "9852320258");
        dataMap.put("nonce_str", WXPayUtil.generateNonceStr());

        //获取签名并转成xml格式
        String paraXml=WXPayUtil.generateSignedXml(dataMap,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");

        //HttpClient
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        HttpClient httpClient = new HttpClient(url);

        //https请求
        httpClient.setHttps(true);

        //设置请求参数
        httpClient.setXmlParam(paraXml);

        //发送请求
        httpClient.post();

        //获取响应数据
        String content=httpClient.getContent();
        System.out.println(content);

    }

    /**
     * 获取HttpClient对象
     */
    public static void createNative() throws Exception {
        //设置腾讯统一下单的方法所需参数
        Map dataMap = new HashMap();

        dataMap.put("appid", "wx8397f8696b538317");      //应用ID
        dataMap.put("mch_id", "1473426802");             //商户ID
        dataMap.put("nonce_str", WXPayUtil.generateNonceStr());      //随机字符
        dataMap.put("body", "品优购");                      //商品标题
        dataMap.put("out_trade_no", "98521552852");      //商品交易号
        dataMap.put("total_fee", "1");      //交易总金额,单位分
        dataMap.put("spbill_create_ip", "127.0.0.1");      //IP地址
        dataMap.put("notify_url", "http://www.itheima.com");
        dataMap.put("trade_type", "NATIVE");      //支付类型

        //将Map转成xml字符
        String paramXml = WXPayUtil.generateSignedXml(dataMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");//签名


        //创建HttpClient对象
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        HttpClient httpClient = new HttpClient(url);

        //设置https配置
        httpClient.setHttps(true);

        //发送xml数据
        httpClient.setXmlParam(paramXml);

        //发送请求
        httpClient.post();

        //获取返回数据
        String content = httpClient.getContent();

        System.out.println(content);
    }

    public static void main(String[] args) throws Exception {
        createNative();
        //queryPayStatus();
    }
}
