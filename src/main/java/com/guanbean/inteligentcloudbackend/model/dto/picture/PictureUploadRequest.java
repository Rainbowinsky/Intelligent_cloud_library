package com.guanbean.inteligentcloudbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @Title 上传图片请求
 * @Author JidamnGuanBean
 * @Description
 * @Time 下午9:40
 */
@Data
public class PictureUploadRequest implements Serializable {
  
    /**  
     * 图片 id（用于修改）  
     */  
    private Long id;

    /**
     * 图片url地址
     */
    private String fileUrl;

    /**
     * 图片名称
     */
    private String picName;
  
    private static final long serialVersionUID = 1L;  
}
