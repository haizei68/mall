package com.pinyougou.seckill.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/login")
public class LoginLoadingController {

    /**
     * 地址欄跳轉
     * @param request
     * @return
     */
    @RequestMapping(value = "/loading")
    public  String load(HttpServletRequest request){
        //网址来源
        String referer = request.getHeader("referer");

        if (StringUtils.isNotBlank(referer)){
            return "redirect:"+referer;
        }
        return "redirect:/seckill-index.html";
    }
}
