package com.guanbean.inteligentcloudbackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * @Title
 * @Author JidamnGuanBean
 * @Description
 * @Time 下午7:27
 */
@Getter
public enum UserRoleEnum {

    USER("用户","user"),
    ADMIN("管理员","admin");

    private final String text;
    private final String value;

    UserRoleEnum(String text,String value){
        this.text = text;
        this.value = value;

    }

    /**
     * 根据value获取枚举
     * @param value
     * @return
     */
    public  static UserRoleEnum getEnumByValue(String value){
        if(ObjUtil.isEmpty(value)){
            return null;
        }
        for(UserRoleEnum userRoleEnum : UserRoleEnum.values()){
            if(userRoleEnum.value.equals(value)){
                return userRoleEnum;
            }
        }
        return null;
    }
}
