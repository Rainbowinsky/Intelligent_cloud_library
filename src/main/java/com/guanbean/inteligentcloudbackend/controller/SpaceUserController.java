package com.guanbean.inteligentcloudbackend.controller;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guanbean.inteligentcloudbackend.common.BaseResponse;
import com.guanbean.inteligentcloudbackend.common.DeleteRequest;
import com.guanbean.inteligentcloudbackend.common.ResultUtils;
import com.guanbean.inteligentcloudbackend.exception.BusinessException;
import com.guanbean.inteligentcloudbackend.exception.ErrorCode;
import com.guanbean.inteligentcloudbackend.exception.ThrowUtils;
import com.guanbean.inteligentcloudbackend.manager.auth.annotation.SaSpaceCheckPermission;
import com.guanbean.inteligentcloudbackend.manager.auth.model.SpaceUserPermissionConstant;
import com.guanbean.inteligentcloudbackend.model.dto.space.SpaceQueryRequest;
import com.guanbean.inteligentcloudbackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.guanbean.inteligentcloudbackend.model.dto.spaceuser.SpaceUserEditRequest;
import com.guanbean.inteligentcloudbackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.guanbean.inteligentcloudbackend.model.entity.SpaceUser;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.vo.SpaceUserVO;
import com.guanbean.inteligentcloudbackend.service.SpaceUserService;
import com.guanbean.inteligentcloudbackend.service.UserService;
import io.github.classgraph.utils.LogNode;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import java.util.List;

/**
 * @Title
 * @Author JidamnGuanBean
 * @Description
 * @Time 下午4:22
 */
@RestController
@RequestMapping("/spaceUser")
public class SpaceUserController {

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private UserService userService;

    /**
     * 添加空间用户
     * @param spaceUserAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<Long> addSpaceUser(@RequestBody SpaceUserAddRequest spaceUserAddRequest, HttpServletRequest request){
        ThrowUtils.throwIf(spaceUserAddRequest==null, ErrorCode.PARAMS_ERROR);
        long id = spaceUserService.addSpaceUser(spaceUserAddRequest);
        return ResultUtils.success(id);
    }

    /**
     * 移除空间成员
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest,HttpServletRequest request){
        ThrowUtils.throwIf(deleteRequest==null,ErrorCode.PARAMS_ERROR);

        //判断id是否存在
        long userid = deleteRequest.getId();
        User spaceUser = userService.getById(userid);
        if(ObjUtil.isEmpty(spaceUser)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        //操作数据库
        boolean result = spaceUserService.removeById(userid);
        ThrowUtils.throwIf(result,ErrorCode.OPERATION_ERROR,"操作失败");
        return ResultUtils.success(true);
    }

    /**
     * 查看某个成员在空间的详细信息
     * @param queryRequest
     * @return
     */
    @PostMapping("/get")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    public BaseResponse<SpaceUser> querySpaceUser(@RequestBody SpaceUserQueryRequest queryRequest ){
        //校验参数
        ThrowUtils.throwIf(queryRequest ==null,ErrorCode.PARAMS_ERROR,"请求参数为空");

        Long userId = queryRequest.getUserId();
        Long spaceId = queryRequest.getSpaceId();

        ThrowUtils.throwIf(ObjectUtil.hasEmpty(userId,spaceId),ErrorCode.PARAMS_ERROR,"用户或空间不存在");
        //查询数据库
        SpaceUser spaceUser = spaceUserService.getOne(spaceUserService.getQueryWrapper(queryRequest));
        ThrowUtils.throwIf(ObjUtil.isEmpty(spaceUser),ErrorCode.PARAMS_ERROR,"未查询到此用户");
        return ResultUtils.success(spaceUser);
    }

    /**
     * 查看当前空间成员列表
     * @param queryRequest
     * @param request
     * @return
     */
    @PostMapping("/list")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    private BaseResponse<List<SpaceUserVO>> ListSpaceUser(@RequestBody SpaceUserQueryRequest queryRequest, HttpServletRequest request){
        ThrowUtils.throwIf(queryRequest ==null,ErrorCode.PARAMS_ERROR);
        List<SpaceUser> spaceUserList = spaceUserService.list(
                spaceUserService.getQueryWrapper(queryRequest)
        );
        return ResultUtils.success(spaceUserService.getSpaceUserVOList(spaceUserList));
    }

    /**
     * 编辑成员信息
     * @param spaceUserEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    @SaSpaceCheckPermission(value = SpaceUserPermissionConstant.SPACE_USER_MANAGE)
    private BaseResponse<Boolean> editSpaceUser(@RequestBody SpaceUserEditRequest spaceUserEditRequest,HttpServletRequest request){
        ThrowUtils.throwIf(spaceUserEditRequest ==null||spaceUserEditRequest.getId()<0,ErrorCode.PARAMS_ERROR);
        //将实体类和DTO进行转换
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserEditRequest,spaceUser);
        //数据校验
        spaceUserService.validSpaceUser(spaceUser,false);
        //判断是否存在
        long id = spaceUserEditRequest.getId();
        SpaceUser oldSpaceUser = spaceUserService.getById(id);
        if(ObjUtil.isEmpty(oldSpaceUser)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        boolean result = spaceUserService.updateById(spaceUser);
        return ResultUtils.success(result);
    }

    /**
     * 查询我加入的团队空间列表
     * @param request
     * @return
     */
    @PostMapping("/list/my")
    private BaseResponse<List<SpaceUser>> ListMyTeamSpace(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        SpaceUserQueryRequest spaceQueryRequest = new SpaceUserQueryRequest();
        spaceQueryRequest.setUserId(loginUser.getId());
        List<SpaceUser> spaceUserList = spaceUserService.list(
                spaceUserService.getQueryWrapper(spaceQueryRequest)
        );
        return ResultUtils.success(spaceUserList);
    }
}
