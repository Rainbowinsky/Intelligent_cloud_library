package com.guanbean.inteligentcloudbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @Title 用户上传头像请求
 * @Author JidamnGuanBean
 * @Description
 * @Time 下午8:06
 */
@Data
public class UserAvatarUploadRequest implements Serializable {
    /**
     * 图片url
     */
     String url;

    /**
     * 用户id
     */
    Long UserId;


    private static final long serialVersionUID = 1L;
}
