package com.itheima.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class HelloWorldController {
    @Autowired
    private Environment environment;

    @RequestMapping(value="/info")
    public String info(){
        return "HelloWorld~";
    }
}
