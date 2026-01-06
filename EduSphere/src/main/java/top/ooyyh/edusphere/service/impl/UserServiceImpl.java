package top.ooyyh.edusphere.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ooyyh.edusphere.entity.User;
import top.ooyyh.edusphere.mapper.UserMapper;
import top.ooyyh.edusphere.request.PasswordUpdateRequest;
import top.ooyyh.edusphere.request.UserUpdateRequest;
import top.ooyyh.edusphere.request.UserLoginRequest;
import top.ooyyh.edusphere.request.UserRegisterRequest;
import top.ooyyh.edusphere.service.UserService;
import top.ooyyh.edusphere.utils.Result;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean isUserExist(String username) {
        try {
            return userMapper.isUserExist(username);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int register(UserRegisterRequest request) {
        try {
            return userMapper.register(request);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public User login(UserLoginRequest request) {
        try {
            // 根据用户名、密码和角色查询用户
            User user = userMapper.getUserByNamePassRole(request);
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Result<User> getUserById(Integer userId) {
        try {
            User user = userMapper.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            // 清除敏感信息
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> updateUserProfile(Integer userId, UserUpdateRequest request) {
        try {
            User user = userMapper.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 检查用户名是否已被其他用户使用
            if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
                User existingUser = userMapper.getUserByUsername(request.getUsername());
                if (existingUser != null && !existingUser.getId().equals(userId)) {
                    return Result.error("用户名已被使用");
                }
            }

            // 检查邮箱是否已被其他用户使用
            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                User existingUser = userMapper.getUserByEmail(request.getEmail());
                if (existingUser != null && !existingUser.getId().equals(userId)) {
                    return Result.error("邮箱已被使用");
                }
            }

            // 更新用户信息
            if (request.getUsername() != null) {
                user.setUsername(request.getUsername());
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getAvatar() != null) {
                user.setAvatar(request.getAvatar());
            }
            user.setUpdatedAt(LocalDateTime.now());

            userMapper.updateUser(user);
            return Result.success("个人信息更新成功");
        } catch (Exception e) {
            return Result.error("更新个人信息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<String> updatePassword(Integer userId, PasswordUpdateRequest request) {
        try {
            User user = userMapper.getUserById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 验证旧密码（简单验证，不加密）
            if (!request.getOldPassword().equals(user.getPassword())) {
                return Result.error("旧密码错误");
            }

            // 更新密码（直接存储，不加密）
            user.setPassword(request.getNewPassword());
            user.setUpdatedAt(LocalDateTime.now());

            userMapper.updateUser(user);
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error("修改密码失败: " + e.getMessage());
        }
    }
}