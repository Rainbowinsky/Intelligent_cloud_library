package com.guanbean.inteligentcloudbackend.service;

import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author MrJeson
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-08-28 18:15:45
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return  新用户id
     */
    long userRegister(String userAccount,String userPassword,String checkPassword);
    public String getEncryptPassword(String userPassword);
}
