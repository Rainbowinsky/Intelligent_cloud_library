package com.guanbean.inteligentcloudbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guanbean.inteligentcloudbackend.model.dto.space.analyze.*;
import com.guanbean.inteligentcloudbackend.model.entity.Space;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.vo.space.analyze.*;

import java.util.List;

/**
 * @Title
 * @Author JidamnGuanBean
 * @Description
 * @Time 下午5:21
 */
public interface SpaceAnalyzeService extends IService<Space> {

    /**
     * 校验权限
     * @param spaceAnalyzeRequest
     * @param loginUser
     */
    void checkSpaceAnalyzeAuth(SpaceAnalyzeRequest spaceAnalyzeRequest, User loginUser);

    /**
     * 获取空间参数信息
     * @param spaceUsageAnalyzeRequest
     * @param loginUser
     * @return
     */
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyzer(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser);

    /**
     * 获取分类的参数信息
     * @param spaceCategoryAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest,User loginUser);

    /**
     * 检查空间权限
     * @param loginUser
     * @param space
     */
    void checkSpaceAuth(User loginUser, Space space);

    /**
     * 获取标签分类信息
     * @param spaceTagAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest,User loginUser);

    /**
     * 获取空间大小
     * @param spaceSizeAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);

    /**
     * 获取用户上传信息
     * @param spaceUserAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);

    /**
     * 空间使用排行分析
     * @param spaceRankAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);

}
