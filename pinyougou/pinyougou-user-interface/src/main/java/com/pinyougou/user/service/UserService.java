package com.pinyougou.user.service;
import com.github.pagehelper.PageInfo;
import com.pinyougou.model.User;
import java.util.List;

public interface UserService {

    /***
     * 增加User信息
     * @param user
     * @return
     */
    int add(User user);

    /**
     * 验证用户名是否被占用
     * @param username
     * @return
     */
    User getUserByName(String username);

    /**
     * 创建验证码并发送
     * @param phone
     */
    void createCode(String phone) throws Exception;

    /**
     * 校验验证码
      * @param phone
     * @param code
     * @return
     */
    Boolean checkCode(String phone, String code);
}
