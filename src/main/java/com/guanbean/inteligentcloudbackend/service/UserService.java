package com.guanbean.inteligentcloudbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guanbean.inteligentcloudbackend.model.dto.picture.PictureQueryRequest;
import com.guanbean.inteligentcloudbackend.model.dto.user.UserQueryRequest;
import com.guanbean.inteligentcloudbackend.model.entity.Picture;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanbean.inteligentcloudbackend.model.vo.LoginUserVO;
import com.guanbean.inteligentcloudbackend.model.vo.PictureVO;
import com.guanbean.inteligentcloudbackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    /**
     * 用户登出
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取用户脱敏后的信息
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取用户脱敏后的信息列表
     * @param list
     * @return
     */
    List<UserVO> getUserVOList(List<User> list);

    /**
     * 返回添加用户请求的查询格式
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);


    /**
     * 判断用户是否为管理员
     * @param user
     * @return
     */
    boolean isAdmin(User user);

}
