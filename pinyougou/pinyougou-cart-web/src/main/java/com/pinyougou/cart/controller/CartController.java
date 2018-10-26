package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.http.Result;
import com.pinyougou.model.Cart;
import com.pinyougou.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private HttpServletRequest request;

    /**
     * 购物车列表查询
     * @return
     *
     */
    @RequestMapping(value = "/list")
    public List<Cart> list(){
        //获取用户匿名账号
        String username=SecurityContextHolder.getContext().getAuthentication().getName();

        //用户未登录
        List<Cart> cookiecarts = new ArrayList<Cart>();

        //未登录,到Cookie取数据
        String cartstr = CookieUtil.getCookieValue(request, "CartLIst", "UTF-8");

        //将字符串转成List<Cart>
        if (StringUtils.isNotBlank(cartstr)){
            cookiecarts=JSON.parseArray(cartstr,Cart.class);

        }

        //没登录则从Cookie查询
        if ("anonymousUser".equals(username)){
            return cookiecarts;
        }else {
            //用户如果已经登录,则购物车数据从Reids缓存中查询
            List<Cart> rediscarts = cartService.findGoodsFromRedis(username);

            //判断Cookie中是否存在购物车数据,如果存在,合并
            if (cookiecarts!=null && cookiecarts.size()>0){
                //合并
                rediscarts = cartService.megerCart(rediscarts,cookiecarts);

                //清空Cookie中的购物车数据
                CookieUtil.deleteCookie(request,response,"CartLIst");

                //将数据存入到Redis中
                cartService.addGoodsToRedis(username,rediscarts);

            }
            return rediscarts;
        }
    }

    /**
     *
     * @param itemid 需要购买的商品ID
     * @param num      购买的数量
     * @return
     */
    @CrossOrigin(origins="http://localhost:18089",allowCredentials="true")
    @RequestMapping(value = "/add")
    public Result add(Long itemid, Integer num){
        //解决跨域问题
        //response.setHeader("Access-Control-Allow-Origin", "http://localhost:18089");//表示允许哪个域名的访问请求
       // response.setHeader("Access-Control-Allow-Credentials", "true");              //表示是否跨域请求向该工程发送Cookie数据


        //获取用户匿名账号
        String username=SecurityContextHolder.getContext().getAuthentication().getName();

        //获取购物车集合
        List<Cart> carts = list();

        //未登录
        if ("anonymousUser".equals(username)){
            //加入购物车操作
            carts=cartService.add(carts,itemid,num);

            //将购物车数据转成JSON格式
            String jsoncarts= JSON.toJSONString(carts);

            //用户未登录,购物车数据存入到Cookie中
            CookieUtil.setCookie(request,response,
                    "CartLIst",     //购物车在Cookie中的key
                    jsoncarts,                     //购物车在Cookie中的value
                    3600*24*30,     //Cookie的生命周期
                    "UTF-8"         //编码格式
            );

        }else {
            //已登录
            //加入购物车操作
            carts=cartService.add(carts,itemid,num);

            //将数据压入到Redis缓存
            cartService.addGoodsToRedis(username,carts);

        }


        return new Result(true,"添加购物车成功!");

    }


}
