package com.guanbean.inteligentcloudbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanbean.inteligentcloudbackend.model.dto.space.SpaceAddRequest;
import com.guanbean.inteligentcloudbackend.model.dto.space.SpaceQueryRequest;
import com.guanbean.inteligentcloudbackend.model.entity.Space;
import com.guanbean.inteligentcloudbackend.model.entity.Space;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.vo.SpaceVO;
import com.guanbean.inteligentcloudbackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author MrJeson
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-09-09 13:09:01
*/
public interface SpaceService extends IService<Space> {
    /**
     * 校验空间参数，判断是创建空间还是更新空间
     * @param space
     * @param add
     */
    void validSpace(Space space, boolean add);

    /**
     * 填充空间参数
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);


    /**
     * 获取查询空间的查询构建
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 获取空间视图
     * @param space
     * @param request
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 分页获取空间视图
     * @param page
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> page, HttpServletRequest request);

    /**
     * 添加空间方法
     * @param spaceAddRequest
     * @param LoginUser
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User LoginUser);
}
