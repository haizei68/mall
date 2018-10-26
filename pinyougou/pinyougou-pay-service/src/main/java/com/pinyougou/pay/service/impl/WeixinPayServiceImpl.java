package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.util.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {


    /**
     * 查询订单状态
     * @param out_trade_no
     * @return
     */


    /**
     * 创建二维码,返回支付地址信息
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        try {
            //设置腾讯统一下单的方法所需参数
            Map dataMap = new HashMap();

            dataMap.put("appid", "wx8397f8696b538317");      //应用ID
            dataMap.put("mch_id", "1473426802");             //商户ID
            dataMap.put("nonce_str", WXPayUtil.generateNonceStr());      //随机字符
            dataMap.put("body", "品优购");                      //商品标题
            dataMap.put("out_trade_no", out_trade_no);      //商品交易号
            dataMap.put("total_fee", total_fee);      //交易总金额,单位分
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

            //获取响应数据
            String content = httpClient.getContent();

            //将xml转成Map
            Map<String, String> responseDate= WXPayUtil.xmlToMap(content);

            //获取指定数据返回
            Map<String,String> response=new HashMap<String, String>();
            response.put("trade_state",responseDate.get("trade_state"));
            response.put("code_url",responseDate.get("code_url"));
            response.put("trade_out_no",out_trade_no);
            response.put("total_fee",total_fee);


            System.out.println(response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        try {
            Map dataMap = new HashMap();
            dataMap.put("appid","wx8397f8696b538317");
            dataMap.put("mch_id","1473426802");
            dataMap.put("out_trade_no",out_trade_no);
            dataMap.put("nonce_str",WXPayUtil.generateNonceStr());

            //获取签名并转成xml格式
            String paraXml = WXPayUtil.generateSignedXml(dataMap,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");

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
            String content = httpClient.getContent();

            //xml转成Map
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 关闭订单
     * @param tradeoutno
     * @return
     */
    @Override
    public Map<String, String> closePay(String tradeoutno) {

        try {
            Map dataMap = new HashMap();
            dataMap.put("appid","wx8397f8696b538317");
            dataMap.put("mch_id","1473426802");
            dataMap.put("out_trade_no",tradeoutno);
            dataMap.put("nonce_str",WXPayUtil.generateNonceStr());

            //获取签名并转成xml格式
            String paraXml = WXPayUtil.generateSignedXml(dataMap,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");

            //HttpClient
            String url = "https://api.mch.weixin.qq.com/pay/closeorder";
            HttpClient httpClient = new HttpClient(url);

            //https请求
            httpClient.setHttps(true);

            //设置请求参数
            httpClient.setXmlParam(paraXml);

            //发送请求
            httpClient.post();

            //获取响应数据
            String content = httpClient.getContent();

            //xml转成Map
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


}
