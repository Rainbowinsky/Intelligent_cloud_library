package com.guanbean.inteligentcloudbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanbean.inteligentcloudbackend.model.dto.picture.PictureQueryRequest;
import com.guanbean.inteligentcloudbackend.model.dto.picture.PictureUploadRequest;
import com.guanbean.inteligentcloudbackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author MrJeson
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-08-30 17:17:18
*/
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 获取查询图片的查询构建
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取图片视图
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片视图
     * @param page
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> page, HttpServletRequest request);

    /**
     * 校验图片
     * @param picture
     */
    void validPicture(Picture picture);
}
