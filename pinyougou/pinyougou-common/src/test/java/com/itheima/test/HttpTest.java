package com.itheima.test;

import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.util.HttpClient;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class HttpTest {

    public static void main1(String[] args) throws IOException, ParseException {
       //参数:    url地址
        HttpClient httpClient = new HttpClient("http://www.itheima.com");

        //设置连接是否是https请求
        httpClient.setHttps(false);

        //发送数据
        httpClient.setXmlParam("");

        //执行请求
        httpClient.post();

        //获取返回数据
        String content = httpClient.getContent();

        System.out.println(content);
    }

    public static void main(String[] args) throws Exception {
        String str = WXPayUtil.generateNonceStr();
        System.out.println(str);


        Map<String,String> dataMap=new HashMap<String, String>();
        dataMap.put("name","小红");
        dataMap.put("age","22");
        String paramXml = WXPayUtil.mapToXml(dataMap);
        System.out.println(paramXml);


        String xml = WXPayUtil.generateSignedXml(dataMap, "asdf");
        System.out.println(xml);


        //xml-Map
        Map<String,String> map=WXPayUtil.xmlToMap(paramXml);
        System.out.println(map);
    }
}
