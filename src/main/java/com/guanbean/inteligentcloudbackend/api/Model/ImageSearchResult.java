package com.guanbean.inteligentcloudbackend.api.Model;

import lombok.Data;

/**
 * @Title
 * @Author JidamnGuanBean
 * @Description
 * @Time 下午5:07
 */
@Data
public class ImageSearchResult {

    /**
     * 缩略图地址
     */
    private String thumbUrl;

    /**
     * 来源地址
     */
    private String fromUrl;
}

