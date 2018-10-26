package com.qq.weixin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/pay")
public class WeixinPayController {

    @RequestMapping(value = "/unifiedorder")
    public Map createUrl(String appid,
                         String mch_id, @RequestParam(value="device_info",required =false )String device_info,
                         String nonce_str){

        return null;
    }

}
