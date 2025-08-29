package com.guanbean.inteligentcloudbackend.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Title 用户登陆实体类
 * @Author JidamnGuanBean
 * @Description
 * @Time 上午12:58
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -5790914933585947424L;
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;

}
