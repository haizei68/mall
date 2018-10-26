package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.model.Address;
import com.pinyougou.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl  implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 根据用户名查询地址列表查询
     * @param userid
     * @return
     */
    @Override
    public List<Address> getAddressListByUserId(String userid) {
        //select * from tb_address where user_id=?
        Address address = new Address();
        address.setUserId(userid);

        return addressMapper.select(address);
    }
}
