package com.guanbean.inteligentcloudbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanbean.inteligentcloudbackend.annotation.AuthCheck;
import com.guanbean.inteligentcloudbackend.common.BaseResponse;
import com.guanbean.inteligentcloudbackend.common.DeleteRequest;
import com.guanbean.inteligentcloudbackend.common.ResultUtils;
import com.guanbean.inteligentcloudbackend.constant.UserConstant;
import com.guanbean.inteligentcloudbackend.exception.BusinessException;
import com.guanbean.inteligentcloudbackend.exception.ErrorCode;
import com.guanbean.inteligentcloudbackend.exception.ThrowUtils;
import com.guanbean.inteligentcloudbackend.model.dto.user.*;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.enums.UserRoleEnum;
import com.guanbean.inteligentcloudbackend.model.vo.LoginUserVO;
import com.guanbean.inteligentcloudbackend.model.vo.PictureVO;
import com.guanbean.inteligentcloudbackend.model.vo.UserVO;
import com.guanbean.inteligentcloudbackend.service.PictureService;
import com.guanbean.inteligentcloudbackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Title 用户接口
 * @Author JidamnGuanBean
 * @Description 用于用户的各类请求
 * @Time 下午10:26
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private PictureService pictureService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest registerRequest){
        ThrowUtils.throwIf(registerRequest==null,new BusinessException(ErrorCode.PARAMS_ERROR));
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String checkPassword = registerRequest.getCheckPassword();
        long result = userService.userRegister(userAccount,userPassword,checkPassword);

        return ResultUtils.success(result);
    }

    /**
     * 用户登陆
     * @param userLoginRequest
     * @return 用户信息(展示给前端的信息)
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        ThrowUtils.throwIf(userLoginRequest==null,new BusinessException(ErrorCode.PARAMS_ERROR));
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        return ResultUtils.success(userService.userLogin(userAccount,userPassword,request));
    }

    /**
     * 获取登陆用户信息
     * @param request
     * @return
     */

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request){
        User user = userService.getLoginUser(request);
        //将用户转换为已加密的数据
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    /**
     * 登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request){
        ThrowUtils.throwIf(request==null,ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(userService.userLogout(request));
    }
    /**
     * 创建用户
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        final String DEFAULT_PASSWORD = "123456789";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

//    /**
//     * 上传头像
//     * @param multipartFile
//     * @param request
//     * @return
//     */
//    @PostMapping("/upload/user_avatar")
//    public BaseResponse<PictureVO> uploadUserAvatar(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request){
//        User loginUser = userService.getLoginUser(request);
//        ThrowUtils.throwIf(multipartFile==null,ErrorCode.PARAMS_ERROR,"头像链接为空");
//        PictureVO pictureVO =pictureService.uploadUserAvatar(multipartFile,loginUser);
//
//        return ResultUtils.success(pictureVO);
//    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }


}
