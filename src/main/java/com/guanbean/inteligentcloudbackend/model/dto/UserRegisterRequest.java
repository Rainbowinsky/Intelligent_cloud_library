package com.guanbean.inteligentcloudbackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Title 用户注册请求
 * @Author JidamnGuanBean
 * @Description
 * @Time 下午9:32
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 6146846757326298928L;
    //账号
    private String userAccount;
    //密码
    private String userPassword;
    //确认密码
    private String checkPassword;

}
