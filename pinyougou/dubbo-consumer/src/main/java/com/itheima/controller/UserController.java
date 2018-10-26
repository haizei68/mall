package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    @Reference
    private UserService userService;

    @ResponseBody
    @RequestMapping(value = "/name")
    public String getUserName(){
        String  result = userService.getUserName();
        return  result;
    }

}
