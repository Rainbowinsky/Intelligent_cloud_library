package com.guanbean.inteligentcloudbackend.service.serviceImpl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanbean.inteligentcloudbackend.exception.BusinessException;
import com.guanbean.inteligentcloudbackend.exception.ErrorCode;
import com.guanbean.inteligentcloudbackend.exception.ThrowUtils;
import com.guanbean.inteligentcloudbackend.mapper.SpaceMapper;
import com.guanbean.inteligentcloudbackend.model.dto.space.analyze.*;
import com.guanbean.inteligentcloudbackend.model.entity.Picture;
import com.guanbean.inteligentcloudbackend.model.entity.Space;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.vo.space.analyze.*;
import com.guanbean.inteligentcloudbackend.service.PictureService;
import com.guanbean.inteligentcloudbackend.service.SpaceAnalyzeService;
import com.guanbean.inteligentcloudbackend.service.SpaceService;
import com.guanbean.inteligentcloudbackend.service.UserService;
import org.aspectj.lang.annotation.DeclareWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.Resource;
import javax.print.attribute.standard.NumberUp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Title
 * @Author JidamnGuanBean
 * @Description
 * @Time 下午5:23
 */
@Service
public class SpaceAnalyzeServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceAnalyzeService {

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private PictureService pictureService;
    @Qualifier("resourceHandlerMapping")
    @Autowired
    private HandlerMapping resourceHandlerMapping;


    public void checkSpaceAnalyzeAuth(SpaceAnalyzeRequest spaceAnalyzeRequest, User loginUser) {
        // 检查权限
        if (spaceAnalyzeRequest.isQueryAll() || spaceAnalyzeRequest.isQueryPublic()) {
            // 全空间分析或者公共图库权限校验：仅管理员可访问
            ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR, "无权访问公共图库");
        } else {
            // 私有空间权限校验
            Long spaceId = spaceAnalyzeRequest.getSpaceId();
            ThrowUtils.throwIf(spaceId == null || spaceId <= 0, ErrorCode.PARAMS_ERROR);
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            spaceService.checkSpaceAuth(loginUser, space);
        }
    }



    private static void fillAnalyzeQueryWrapper(SpaceAnalyzeRequest spaceAnalyzeRequest, QueryWrapper<Picture> queryWrapper) {
        if (spaceAnalyzeRequest.isQueryAll()) {
            return;
        }
        if (spaceAnalyzeRequest.isQueryPublic()) {
            queryWrapper.isNull("spaceId");
            return;
        }
        Long spaceId = spaceAnalyzeRequest.getSpaceId();
        if (spaceId != null) {
            queryWrapper.eq("spaceId", spaceId);
            return;
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未指定查询范围");
    }

    /**
     * 获取空间使用情况分析
     * @param spaceUsageAnalyzeRequest
     * @param loginUser
     * @return
     */

    public SpaceUsageAnalyzeResponse getSpaceUsageAnalyzer(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, User loginUser){
        //校验参数

        //全空间或公共图库 需要从picture查询
        if(spaceUsageAnalyzeRequest.isQueryAll() || spaceUsageAnalyzeRequest.isQueryPublic()){
            //仅管理员可用
            checkSpaceAnalyzeAuth(spaceUsageAnalyzeRequest,loginUser);
            //统计图库使用空间
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("pictureSize");
            //补充查询范围
            fillAnalyzeQueryWrapper(spaceUsageAnalyzeRequest,queryWrapper);
            List<Object> pictureObjList = pictureService.getBaseMapper().selectObjs(queryWrapper);
            long useSize = pictureObjList.stream().mapToLong(obj->(Long) obj).sum();
            long userCount = pictureObjList.size();
            SpaceUsageAnalyzeResponse spaceUsageAnalyzeResponse = new SpaceUsageAnalyzeResponse();
            spaceUsageAnalyzeResponse.setUsedCount(userCount);
            spaceUsageAnalyzeResponse.setUsedSize(useSize);
            spaceUsageAnalyzeResponse.setMaxSize(null);
            spaceUsageAnalyzeResponse.setMaxCount(null);
            spaceUsageAnalyzeResponse.setSizeUsageRatio(null);
            spaceUsageAnalyzeResponse.setCountUsageRatio(null);
            return spaceUsageAnalyzeResponse;

        }else {
            Long spaceId = spaceUsageAnalyzeRequest.getSpaceId();
            Space space = spaceService.getById(spaceId);
            long usedSize = space.getTotalSize();
            long usedCount = space.getTotalCount();
            SpaceUsageAnalyzeResponse spaceUsageAnalyzeResponse = new SpaceUsageAnalyzeResponse();
            spaceUsageAnalyzeResponse.setUsedCount(usedCount);
            spaceUsageAnalyzeResponse.setUsedSize(usedSize);
            spaceUsageAnalyzeResponse.setMaxSize(space.getMaxSize());
            spaceUsageAnalyzeResponse.setMaxCount(space.getMaxCount());

            //计算比例
            double sizeUsageRatio = NumberUtil.round(space.getTotalSize()* 100.0/space.getMaxSize(),2).doubleValue();
            double countUsageRatio = NumberUtil.round(space.getTotalCount()*100.0/space.getMaxCount(),2).doubleValue();
            spaceUsageAnalyzeResponse.setSizeUsageRatio(sizeUsageRatio);
            spaceUsageAnalyzeResponse.setCountUsageRatio(countUsageRatio);
            return spaceUsageAnalyzeResponse;
        }
    }

    /**
     * 统计分类参数信息
     * @param spaceCategoryAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, User loginUser) {
        //权限校验
        ThrowUtils.throwIf(ObjUtil.isEmpty(spaceCategoryAnalyzeRequest),ErrorCode.PARAMS_ERROR);
        checkSpaceAnalyzeAuth(spaceCategoryAnalyzeRequest,loginUser);

        //构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceCategoryAnalyzeRequest,queryWrapper);
        queryWrapper.select("category","count(*) as count ","sum(picSize) as totalSize")
                .groupBy("category");

        //查询
        return pictureService .getBaseMapper().selectMaps(queryWrapper)
                .stream()
                .map(result->{
                    String category = (String) result.get("category");
                    Long count = ((Number) result.get("count")).longValue();
                    Long totalSize = ((Number) result.get("totalSize")).longValue();
                    return new SpaceCategoryAnalyzeResponse(category,count,totalSize);
                }).collect(Collectors.toList());
    }

    /**
     * 空间权限校验
     *
     * @param loginUser
     * @param space
     */
    @Override
    public void checkSpaceAuth(User loginUser, Space space) {
        // 仅本人或管理员可访问
        if (!space.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }

    /**
     * 按照标签统计
     * @param spaceTagAnalyzeRequest
     * @param loginUser
     * @return
     */

    @Override
    public List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 检查权限
        checkSpaceAnalyzeAuth(spaceTagAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceTagAnalyzeRequest, queryWrapper);

        // 查询所有符合条件的标签
        queryWrapper.select("tags");
        List<String> tagsJsonList = pictureService.getBaseMapper().selectObjs(queryWrapper)
                .stream()
                .filter(ObjUtil::isNotNull)
                .map(Object::toString)
                .collect(Collectors.toList());

        // 合并所有标签并统计使用次数
        Map<String, Long> tagCountMap = tagsJsonList.stream()
                .flatMap(tagsJson -> JSONUtil.toList(tagsJson, String.class).stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));

        // 转换为响应对象，按使用次数降序排序
        return tagCountMap.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // 降序排列
                .map(entry -> new SpaceTagAnalyzeResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 获取空间分类大小
     * @param spaceSizeAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

        // 检查权限
        checkSpaceAnalyzeAuth(spaceSizeAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(spaceSizeAnalyzeRequest, queryWrapper);

        // 查询所有符合条件的图片大小
        queryWrapper.select("picSize");
        List<Long> picSizes = pictureService.getBaseMapper().selectObjs(queryWrapper)
                .stream()
                .map(size -> ((Number) size).longValue())
                .collect(Collectors.toList());

        // 定义分段范围，注意使用有序 Map
        Map<String, Long> sizeRanges = new LinkedHashMap<>();
        sizeRanges.put("<100KB", picSizes.stream().filter(size -> size < 100 * 1024).count());
        sizeRanges.put("100KB-500KB", picSizes.stream().filter(size -> size >= 100 * 1024 && size < 500 * 1024).count());
        sizeRanges.put("500KB-1MB", picSizes.stream().filter(size -> size >= 500 * 1024 && size < 1 * 1024 * 1024).count());
        sizeRanges.put(">1MB", picSizes.stream().filter(size -> size >= 1 * 1024 * 1024).count());

        // 转换为响应对象
        return sizeRanges.entrySet().stream()
                .map(entry -> new SpaceSizeAnalyzeResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户上传信息
     * @param spaceUserAnalyzeRequest
     * @param loginUser
     * @return
     */
    @Override
    public List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        // 检查权限
        checkSpaceAnalyzeAuth(spaceUserAnalyzeRequest, loginUser);

        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        Long userId = spaceUserAnalyzeRequest.getUserId();
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        fillAnalyzeQueryWrapper(spaceUserAnalyzeRequest, queryWrapper);

        // 分析维度：每日、每周、每月
        String timeDimension = spaceUserAnalyzeRequest.getTimeDimension();
        switch (timeDimension) {
            case "day":
                queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m-%d') AS period", "COUNT(*) AS count");
                break;
            case "week":
                queryWrapper.select("YEARWEEK(createTime) AS period", "COUNT(*) AS count");
                break;
            case "month":
                queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m') AS period", "COUNT(*) AS count");
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的时间维度");
        }

        // 分组和排序
        queryWrapper.groupBy("period").orderByAsc("period");

        // 查询结果并转换
        List<Map<String, Object>> queryResult = pictureService.getBaseMapper().selectMaps(queryWrapper);
        return queryResult.stream()
                .map(result -> {
                    String period = result.get("period").toString();
                    Long count = ((Number) result.get("count")).longValue();
                    return new SpaceUserAnalyzeResponse(period, count);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser) {
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);

// 仅管理员可查看空间排行
        ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR, "无权查看空间排行");

// 构造查询条件
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "spaceName", "userId", "totalSize")
                .orderByDesc("totalSize")
                .last("LIMIT " + spaceRankAnalyzeRequest.getTopN()); // 取前 N 名

// 查询结果
        return spaceService.list(queryWrapper);
    }






}
