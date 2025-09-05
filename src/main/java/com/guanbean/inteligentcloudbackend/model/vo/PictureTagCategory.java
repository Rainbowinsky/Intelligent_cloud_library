package com.guanbean.inteligentcloudbackend.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @Title
 * @Author JidamnGuanBean
 * @Description
 * @Time 下午2:41
 */
@Data
public class PictureTagCategory {
    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 分类列表
     */

    private List<String> categoryList;
}
