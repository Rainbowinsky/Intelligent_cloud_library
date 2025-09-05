package com.guanbean.inteligentcloudbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * @Title
 * @Author JidamnGuanBean
 * @Description 抓取图片上传请求
 * @Time 下午12:54
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer count = 10;

    /**
     * 名称前缀
     */
    private String namePrefix;


    private static final long serialVersionUID = 1L;
}
