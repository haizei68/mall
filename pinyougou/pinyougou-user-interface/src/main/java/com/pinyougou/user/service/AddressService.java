package com.pinyougou.user.service;

import com.pinyougou.model.Address;

import java.util.List;

public interface AddressService {

    /**
     * 根据用户名查询地址列表查询
     * @param userid
     * @return
     */
    List<Address> getAddressListByUserId(String userid);
}
