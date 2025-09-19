package com.guanbean.inteligentcloudbackend.service.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanbean.inteligentcloudbackend.exception.BusinessException;
import com.guanbean.inteligentcloudbackend.exception.ErrorCode;
import com.guanbean.inteligentcloudbackend.manager.auth.StpKit;
import com.guanbean.inteligentcloudbackend.model.dto.user.UserQueryRequest;
import com.guanbean.inteligentcloudbackend.model.dto.user.UserUpdateRequest;
import com.guanbean.inteligentcloudbackend.model.entity.User;
import com.guanbean.inteligentcloudbackend.model.enums.UserRoleEnum;
import com.guanbean.inteligentcloudbackend.model.vo.LoginUserVO;
import com.guanbean.inteligentcloudbackend.model.vo.UserVO;
import com.guanbean.inteligentcloudbackend.service.UserService;
import com.guanbean.inteligentcloudbackend.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.guanbean.inteligentcloudbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author MrJeson
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-08-28 18:15:45
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 用户注册
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 用户id
     */

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
        queryWrapper.eq("userAccount",userAccount);
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

    /**
     * 加密
     * @param userPassword
     * @return 加密后的密码
     */

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "jidamnguanbean";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return 登陆的用户信息
     */

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验格式
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号太短");
        }
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码太短");
        }
        //校验账号是否存在
        //判断密码是否正确
        //加密密码
        String encryptPassword = getEncryptPassword(userPassword);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        if(user==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不存在或者密码错误");
        }

        //执行登陆逻辑
        //保存用户的登陆态
        request.getSession().setAttribute(USER_LOGIN_STATE,user);
        //保存登陆态到sa-token
        StpKit.SPACE.login(user.getId());
        StpKit.SPACE.getSession().set(USER_LOGIN_STATE,user);
        return this.getLoginUserVO(user);
    }

    /**
     * 获取脱敏后的数据
     * @param user
     * @return 脱敏后的用户信息
     */

    @Override
    public LoginUserVO getLoginUserVO(User user) {

        if(user==null){
            return  null;
        }

        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     *获取登陆用户信息
     * @param request
     * @return 用户信息
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        //先判断是否登陆
        Object obj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) obj;
        if(currentUser == null||currentUser.getId() ==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //从数据库查询
        long userId = currentUser.getId();
        currentUser = this.baseMapper.selectById(userId);
        return  currentUser;
    }

    /**
     * 用户登出
     * @param request
     * @return 是否登出成功
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        //判断用户是否已经登陆
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userObj==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        //移除登陆状态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 获取用户脱敏后的信息
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        if(user == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return userVO;
    }

    /**
     * 将列表用户全部脱敏
     * @param list
     * @return
     */
    @Override
    public List<UserVO> getUserVOList(List<User> list) {
        if(CollUtil.isEmpty(list)){
            return new ArrayList<>();
        }
        return list.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    /**
     * 返回查询请求的查询构建
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 判断用户是否为管理员
     * @param user
     * @return
     */
    @Override
    public boolean isAdmin(User user) {

        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest
     * @return
     */

    @Override
    public User userUpdateRequest(UserUpdateRequest userUpdateRequest, Long id) {
        User user = new User();
        String userProfile = userUpdateRequest.getUserProfile();
        String userName = userUpdateRequest.getUserName();
        user.setUserProfile(userProfile);
        user.setUserName(userName);
        user.setId(id);

        this.updateById(user);
        return user;
    }


}




