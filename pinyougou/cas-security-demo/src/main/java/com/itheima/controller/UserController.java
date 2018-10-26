package com.itheima.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    /**
     * 获取用户名
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/name")
    public String getName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
