package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/login")
public class LoginController {

    /**
     * 获取登录名
     * @return
     */
    @RequestMapping(value = "/name")
    public String getUserName(){
        return SecurityContextHolder.getContext().getAuthentication().getName();

    }

}
