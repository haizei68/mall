package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.model.Address;
import com.pinyougou.user.service.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/address")
public class AddressController {

    @Reference
    private AddressService addressService;

    /**
     * 根据用户ID
     * @return
     */
    @RequestMapping(value="/user/list")
    public List<Address> getAddressByUserId(){
        //获取用户名
        String username=SecurityContextHolder.getContext().getAuthentication().getName();

        return addressService.getAddressListByUserId(username);
    }

}
