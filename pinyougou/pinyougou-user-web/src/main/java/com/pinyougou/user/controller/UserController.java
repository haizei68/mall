package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.model.User;
import com.pinyougou.user.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 获取用户登录名
     * @return
     */
    @RequestMapping(value="/login/name")
    public String userName(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    @RequestMapping(value = "/createCode",method = RequestMethod.GET)
    public Result createCode(String  phone){
        try {
            //创建验证码并发送
            userService.createCode(phone);
            return  new Result(true,"发送验证码成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Result(false,"发送验证码失败");
    }




    /***
     * 增加User数据
     * @param user
     * 响应数据：success
     *                  true:成功  false：失败
     *           message
     *                  响应的消息
     *
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public Result add(@RequestBody User user,String code){
        try {
            //验证用户是否被占用
            User userinfo=userService.getUserByName(user.getUsername());
            if(userinfo!=null){
                return new Result(false,"该账号已存在");
            }

            //验证校验码是否正确
            Boolean bo=userService.checkCode(user.getPhone(),code);

            if (!bo){
                return new Result(false,"验证码有误");
            }



            //执行增加
            int acount = userService.add(user);

            if(acount>0){
                //增加成功
               return new Result(true,"增加成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"增加失败");
    }



}
