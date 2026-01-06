package top.ooyyh.edusphere.service;

import top.ooyyh.edusphere.entity.User;
import top.ooyyh.edusphere.request.PasswordUpdateRequest;
import top.ooyyh.edusphere.request.UserUpdateRequest;
import top.ooyyh.edusphere.request.UserLoginRequest;
import top.ooyyh.edusphere.request.UserRegisterRequest;
import top.ooyyh.edusphere.utils.Result;

public interface UserService {
    
    /**
     * 检查用户是否存在
     */
    Boolean isUserExist(String username);
    
    /**
     * 用户注册
     */
    int register(UserRegisterRequest request);
    
    /**
     * 用户登录
     */
    User login(UserLoginRequest request);
    
    /**
     * 根据ID获取用户信息
     */
    Result<User> getUserById(Integer userId);
    
    /**
     * 更新用户个人信息
     */
    Result<String> updateUserProfile(Integer userId, UserUpdateRequest request);
    
    /**
     * 修改密码
     */
    Result<String> updatePassword(Integer userId, PasswordUpdateRequest request);
}