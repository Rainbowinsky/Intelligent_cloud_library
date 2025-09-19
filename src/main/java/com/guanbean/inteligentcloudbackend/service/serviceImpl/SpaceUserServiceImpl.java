package com.guanbean.inteligentcloudbackend.service.serviceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanbean.inteligentcloudbackend.exception.BusinessException;
import com.guanbean.inteligentcloudbackend.exception.ErrorCode;
import com.guanbean.inteligentcloudbackend.exception.ThrowUtils;
import com.guanbean.inteligentcloudbackend.model.dto.spaceuser.SpaceUserAddRequest;
import com.guanbean.inteligentcloudbackend.model.dto.spaceuser.SpaceUserEditRequest;
import com.guanbean.inteligentcloudbackend.model.dto.spaceuser.SpaceUserQueryRequest;
import com.guanbean.inteligentcloudbackend.model.entity.Space;
import com.guanbean.inteligentcloudbackend.model.entity.SpaceUser;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.enums.SpaceRoleEnum;
import com.guanbean.inteligentcloudbackend.model.vo.SpaceUserVO;
import com.guanbean.inteligentcloudbackend.model.vo.SpaceVO;
import com.guanbean.inteligentcloudbackend.model.vo.UserVO;
import com.guanbean.inteligentcloudbackend.service.SpaceService;
import com.guanbean.inteligentcloudbackend.service.SpaceUserService;
import com.guanbean.inteligentcloudbackend.mapper.SpaceUserMapper;
import com.guanbean.inteligentcloudbackend.service.UserService;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author MrJeson
* @description 针对表【space_user(空间用户关联)】的数据库操作Service实现
* @createDate 2025-09-14 20:28:51
*/
@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
    implements SpaceUserService{

    @Resource
    @Lazy
    private SpaceService spaceService;

    @Resource
    private UserService userService;

    /**
     * 添加空间用户
     * @param spaceUserAddRequest
     * @return
     */
    @Override
    public long addSpaceUser(SpaceUserAddRequest spaceUserAddRequest) {
        //参数校验
        if(spaceUserAddRequest.getUserId()==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"添加的用户不能为空");
        }
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(spaceUserAddRequest,spaceUser);

        //判断此用户是否存在
        boolean isUserExist = userService.query().eq("id",spaceUserAddRequest.getUserId()).exists();
        ThrowUtils.throwIf(!isUserExist,ErrorCode.OPERATION_ERROR,"此用户不存在");
        //查看当前用户id是否存在表中
        boolean isExist = query().eq("userId",spaceUserAddRequest.getUserId()).exists();
        ThrowUtils.throwIf(isExist,ErrorCode.OPERATION_ERROR,"此用户已存在");


        //操作数据库
        boolean result = save(spaceUser);
        ThrowUtils.throwIf(result,ErrorCode.OPERATION_ERROR,"添加用户失败");

        return spaceUser.getId();
    }

    @Override
    public void validSpaceUser(SpaceUser spaceUser, boolean add) {
        ThrowUtils.throwIf(spaceUser == null, ErrorCode.PARAMS_ERROR);
        // 创建时，空间 id 和用户 id 必填
        Long spaceId = spaceUser.getSpaceId();
        Long userId = spaceUser.getUserId();
        if (add) {
            ThrowUtils.throwIf(ObjectUtil.hasEmpty(spaceId, userId), ErrorCode.PARAMS_ERROR);
            User user = userService.getById(userId);
            ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        }
        // 校验空间角色
        String spaceRole = spaceUser.getSpaceRole();
        SpaceRoleEnum spaceRoleEnum = SpaceRoleEnum.getEnumByValue(spaceRole);
        if (spaceRole != null && spaceRoleEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间角色不存在");
        }

        //校验是否已经有了此成员
        boolean result = this.lambdaQuery().eq(SpaceUser::getUserId,userId)
                .eq(SpaceUser::getSpaceId,spaceId)
                .exists();
        if(result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"此成员已存在");
        }
    }

    /**
     * 返回查询构造条件
     * @param spaceUserQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
        QueryWrapper<SpaceUser> queryWrapper = new QueryWrapper<>();
        if(spaceUserQueryRequest == null){
            return queryWrapper;
        }
        Long id = spaceUserQueryRequest.getId();
        Long userId = spaceUserQueryRequest.getUserId();
        Long spaceId = spaceUserQueryRequest.getSpaceId();
        String spaceRole = spaceUserQueryRequest.getSpaceRole();

        queryWrapper.eq(ObjUtil.isNotEmpty(id),"id",id)
                .eq(ObjUtil.isNotEmpty(userId),"userId",userId)
                .eq(ObjUtil.isNotEmpty(spaceId),"spaceId",spaceId)
                .eq(StrUtil.isNotEmpty(spaceRole),"spaceRole",spaceRole);

        return queryWrapper;
    }

    /**
     * 返回空间用户视图
     * @param spaceUser
     * @param request
     * @return
     */
    @Override
    public SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request) {
        //对象转封装类
        SpaceUserVO spaceUserVO = SpaceUserVO.objToVo(spaceUser);

        //查询关联用户信息
        Long userId = spaceUser.getUserId();
        if(userId!=null &&userId>0){
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceUserVO.setUser(userVO);
        }
        //查询关联空间信息
        Long spaceId = spaceUser.getSpaceId();
        if(spaceId !=null &&spaceId>0){
            Space space = spaceService.getById(spaceId);
            SpaceVO spaceVO = spaceService.getSpaceVO(space,request);
            spaceUserVO.setSpace(spaceVO);
        }

        return spaceUserVO;
    }

    /**
     * 获取请求集合
     * @param spaceUsers
     * @return
     */
    @Override
    public List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUsers) {
        if(CollUtil.isEmpty(spaceUsers)){
            return Collections.emptyList();
        }
        //将对象列表转为封装对象列表
        List<SpaceUserVO> spaceUserVOS = spaceUsers.stream().map(SpaceUserVO::objToVo).collect(Collectors.toList());
        //1.收集需要关联查询的用户ID和空间ID
        Set<Long> spaceIds = spaceUsers.stream().map(SpaceUser::getSpaceId).collect(Collectors.toSet());
        Set<Long> userIds = spaceUsers.stream().map(SpaceUser::getUserId).collect(Collectors.toSet());
        //2.批量查询用户和空间
        Map<Long,List<User>> userIdUsersMap = userService.listByIds(userIds).stream().collect(Collectors.groupingBy(User::getId));
        Map<Long,List<Space>> spaceIdUsersMap = spaceService.listByIds(spaceIds).stream().collect(Collectors.groupingBy(Space::getId));

        //3.填充SpaceUserVO的用户和空间信息
        spaceUserVOS.forEach(spaceUserVO -> {
            Long userId = spaceUserVO.getUserId();
            Long spaceId = spaceUserVO.getSpaceId();
            //填充用户信息
            User user = null;
            if(userIdUsersMap.containsKey(userId)){
                user = userIdUsersMap.get(userId).get(0);
            }
            spaceUserVO.setUser(userService.getUserVO(user));

            //填充空间信息
            Space space = null;
            if(spaceIdUsersMap.containsKey(spaceId)){
                space = spaceIdUsersMap.get(spaceId).get(0);
            }
            spaceUserVO.setSpace(SpaceVO.objToVo(space));

        });

        return spaceUserVOS;
    }


}




