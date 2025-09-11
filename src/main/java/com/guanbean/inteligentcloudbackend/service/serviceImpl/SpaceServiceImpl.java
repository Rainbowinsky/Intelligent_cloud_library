package com.guanbean.inteligentcloudbackend.service.serviceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanbean.inteligentcloudbackend.exception.BusinessException;
import com.guanbean.inteligentcloudbackend.exception.ErrorCode;
import com.guanbean.inteligentcloudbackend.exception.ThrowUtils;
import com.guanbean.inteligentcloudbackend.model.dto.space.SpaceAddRequest;
import com.guanbean.inteligentcloudbackend.model.dto.space.SpaceQueryRequest;
import com.guanbean.inteligentcloudbackend.model.entity.Space;
import com.guanbean.inteligentcloudbackend.model.entity.Space;
import com.guanbean.inteligentcloudbackend.model.entity.User;

import com.guanbean.inteligentcloudbackend.model.enums.SpaceLevelEnum;
import com.guanbean.inteligentcloudbackend.model.vo.SpaceVO;
import com.guanbean.inteligentcloudbackend.model.vo.UserVO;
import com.guanbean.inteligentcloudbackend.service.SpaceService;
import com.guanbean.inteligentcloudbackend.mapper.SpaceMapper;
import com.guanbean.inteligentcloudbackend.service.UserService;
import org.apache.ibatis.cache.TransactionalCacheManager;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;



/**
* @author MrJeson
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2025-09-09 13:09:01
*/
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceService{

    @Resource
    private UserService userService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public void validSpace(Space space, boolean add) {
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        // 要创建
        if (add) {
            if (StrUtil.isBlank(spaceName)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            }
            if (spaceLevel == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不能为空");
            }
        }
        // 修改数据时，如果要改空间级别
        if (spaceLevel != null && spaceLevelEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不存在");
        }
        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
    }

    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        // 根据空间级别，自动填充限额
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }

    /**
     * 获取图片的查询构建
     * @param spaceQueryRequest
     * @return
     */

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();

        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel),"spaceLevel",spaceLevel);

        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 获取视图对象
     * @param space
     * @param request
     * @return
     */
    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        // 对象转封装类
        SpaceVO spaceVO = SpaceVO.objToVo(space);
        // 关联查询用户信息
        Long userId = space.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    /**
     * 分页获取图片封装
     */
    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }
        // 对象列表 => 封装对象列表
        List<SpaceVO> spaceVOList = spaceList.stream().map(SpaceVO::objToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User LoginUser) {
        //填充参数默认值
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest,space);
        if(StrUtil.isBlank(spaceAddRequest.getSpaceName())){
            space.setSpaceName("默认空间");
        }
        if(spaceAddRequest.getSpaceLevel()==null){
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        //填充数据
        this.fillSpaceBySpaceLevel(space);
        //数据校验
        this.validSpace(space,true);
        Long userId = LoginUser.getId();
        space.setUserId(userId);
        //权限校验，只有管理员才能创建高级空间
        if(SpaceLevelEnum.COMMON.getValue()!=spaceAddRequest.getSpaceLevel() && !userService.isAdmin(LoginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无权限创建指定级别的空间");
        }
        //加锁。每个用户只能够创建一个空间
        String lock = String.valueOf(userId).intern();//intern方法就是获得常量池里的内容，因为常量池里的变量内容相同的话地址是相同的，可以保证不同用户内容不一样
        synchronized (lock){
            //使用内置事务来保证一段代码内的线程安全
             Long newSpaceId = transactionTemplate.execute(status -> {
                //判断是否已有空间
                boolean isExists = this.lambdaQuery().eq(Space::getUserId,userId)
                        .exists();
                //如果已有空间，就不能创建空间
                ThrowUtils.throwIf(isExists,ErrorCode.PARAMS_ERROR,"无法创建多个私人空间!");

                //如果没有就创建
                boolean result = this.save(space);
                ThrowUtils.throwIf(!result,ErrorCode.PARAMS_ERROR,"保存空间失败");
                return space.getId();
            });
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }


    }

}




