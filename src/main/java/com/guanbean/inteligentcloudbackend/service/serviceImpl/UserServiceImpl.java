package com.guanbean.inteligentcloudbackend.service.serviceImpl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanbean.inteligentcloudbackend.common.BaseResponse;
import com.guanbean.inteligentcloudbackend.exception.BusinessException;
import com.guanbean.inteligentcloudbackend.exception.ErrorCode;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.enums.UserRoleEnum;
import com.guanbean.inteligentcloudbackend.service.UserService;
import com.guanbean.inteligentcloudbackend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

/**
* @author MrJeson
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-08-28 18:15:45
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
    //1.校验参数
        if(StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<=4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户名过短");
        }
        if(userPassword.length()<=8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码过短");
        }
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入密码不一致");

        }

    //2.检查用户账号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if(count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");

        }

    //3.密码加密
    String encryptPassword = getEncryptPassword(userPassword);
    //4.存储在数据库中
    User user = new User();
    user.setUserAccount(userAccount);
    user.setUserName("未命名用户");
    user.setUserPassword(encryptPassword);
    user.setUserRole(UserRoleEnum.USER.getValue());

    boolean isSave = this.save(user);
    if(!isSave){
        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据库开小差了，请重试");
    }else {
        return user.getId();
    }

    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "jidamnguanbean";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

}




