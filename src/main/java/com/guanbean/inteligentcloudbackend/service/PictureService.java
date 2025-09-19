package com.guanbean.inteligentcloudbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanbean.inteligentcloudbackend.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.guanbean.inteligentcloudbackend.model.dto.picture.*;
import com.guanbean.inteligentcloudbackend.model.dto.space.SpaceAddRequest;
import com.guanbean.inteligentcloudbackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.vo.PictureVO;
import com.guanbean.inteligentcloudbackend.model.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author MrJeson
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-08-30 17:17:18
*/
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource,
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

    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 填充审核参数
     * @param picture
     * @param loginUser
     */
    void fillReviewParams(Picture picture,User loginUser);

    /**
     * 批量抓取图片方法
     *
     * @param request
     * @param loginUser
     * @return 上传成功的图片数量
     */
    int uploadPictureByBatch(PictureUploadByBatchRequest request, User loginUser);

    /**
     * 删除图片
     * @param oldPicture
     */
    void clearPictureFile(Picture oldPicture);

    /**
     * 校验空间权限
     * @param loginUser
     * @param picture
     */
    void checkPictureAuth(User loginUser, Picture picture);

    /**
     * 删除空间内的照片
     * @param pictureId
     * @param loginUser
     */
    void deletePicture(long pictureId, User loginUser);

    /**
     * 编辑空间内图片
     * @param pictureEditRequest
     * @param loginUser
     */
    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    /**
     * 根据颜色查询图片
     * @param spaceId
     * @param picColor
     * @param loginUser
     * @return
     */
    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    /**
     * 批量修改图片
     * @param pictureEditByBatchRequest
     * @param loginUser
     */
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    /**
     * AI扩图方法
     * @param createPictureOutPaintingTaskRequest
     * @param loginUser
     * @return
     */
    CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);

//    /**
//     * 上传用户头像
//     * @param inputSource
//     * @param loginUser
//     * @return
//     */
//    PictureVO uploadUserAvatar(Object inputSource, User loginUser);
}
