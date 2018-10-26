package com.itheima.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //构建角色集合
       List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
       authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

       //密码账号信息
       String pwd="123456";
        return new User(username,pwd,authorities);
    }
}
