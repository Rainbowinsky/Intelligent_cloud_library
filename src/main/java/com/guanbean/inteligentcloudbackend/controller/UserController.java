package com.guanbean.inteligentcloudbackend.controller;

import com.guanbean.inteligentcloudbackend.common.BaseResponse;
import com.guanbean.inteligentcloudbackend.common.ResultUtils;
import com.guanbean.inteligentcloudbackend.exception.BusinessException;
import com.guanbean.inteligentcloudbackend.exception.ErrorCode;
import com.guanbean.inteligentcloudbackend.exception.ThrowUtils;
import com.guanbean.inteligentcloudbackend.model.dto.UserLoginRequest;
import com.guanbean.inteligentcloudbackend.model.dto.UserRegisterRequest;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.vo.LoginUserVO;
import com.guanbean.inteligentcloudbackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request){
        User user = userService.getLoginUser(request);
        //将用户转换为已加密的数据
        return ResultUtils.success(userService.getLoginUserVO(user));
    }
}
