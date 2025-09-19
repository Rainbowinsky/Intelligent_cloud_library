package com.guanbean.inteligentcloudbackend.service;

import cn.hutool.http.HttpRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guanbean.inteligentcloudbackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.guanbean.inteligentcloudbackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.guanbean.inteligentcloudbackend.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.vo.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author MrJeson
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2025-09-14 20:28:51
*/
public interface SpaceUserService extends IService<SpaceUser> {
    /**
     * 添加用户
     * @param spaceUserAddRequest
     * @return
     */
    long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest);

    /**
     * 校验空间用户
     * @param spaceUser
     * @param add
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 返回条件构造
     * @param spaceUserQueryRequest
     * @return
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

    /**
     * 返回空间用户视图
     * @param spaceUser
     * @param request
     * @return
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 返回集合
     * @param spaceUsers
     * @return
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUsers);

}
