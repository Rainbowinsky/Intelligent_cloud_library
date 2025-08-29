package com.guanbean.inteligentcloudbackend.service;

import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanbean.inteligentcloudbackend.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 用户登陆
     *
     * @param userAccount
     * @param userPassword
     * @return
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获得脱敏后的用户信息
     *
     * @param user
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 根据session获取用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);
}
