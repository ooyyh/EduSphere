package top.ooyyh.edusphere.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ooyyh.edusphere.entity.User;
import top.ooyyh.edusphere.request.UserUpdateRequest;
import top.ooyyh.edusphere.request.PasswordUpdateRequest;
import top.ooyyh.edusphere.request.UserLoginRequest;
import top.ooyyh.edusphere.request.UserRegisterRequest;
import top.ooyyh.edusphere.service.UserService;
import top.ooyyh.edusphere.utils.Result;
import top.ooyyh.edusphere.utils.JwtUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@ResponseBody
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterRequest request) {
        try {
            // 检查用户名是否已存在
            if (userService.isUserExist(request.getUsername())) {
                return Result.error("用户名已存在");
            }
            
            // 注册用户
            int result = userService.register(request);
            if (result > 0) {
                return Result.success("注册成功");
            } else {
                return Result.error("注册失败");
            }
        } catch (Exception e) {
            return Result.error("注册失败: " + e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody UserLoginRequest request) {
        try {
            // 验证用户名和密码
            User user = userService.login(request);
            if (user == null) {
                return Result.error("用户名或密码错误");
            }

            // 生成JWT token
            String token = JwtUtils.generateToken(user.getUsername(), user.getRole());

            // 准备返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            
            // 创建用户信息Map（兼容Java 8）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("role", user.getRole());
            data.put("user", userInfo);

            return Result.success(data);
        } catch (Exception e) {
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户ID（从JWT中解析）
     */
    private Integer getCurrentUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj != null) {
            return (Integer) userIdObj;
        }
        return -1;
    }

    /**
     * 获取用户个人信息
     */
    @GetMapping("/profile")
    public Result<User> getUserProfile(HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return userService.getUserById(userId);
    }

    /**
     * 更新用户个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateUserProfile(@RequestBody UserUpdateRequest request, HttpServletRequest httpRequest) {
        Integer userId = getCurrentUserId(httpRequest);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return userService.updateUserProfile(userId, request);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody PasswordUpdateRequest request, HttpServletRequest httpRequest) {
        Integer userId = getCurrentUserId(httpRequest);
        if (userId == -1) {
            return Result.error("未登录或登录已过期");
        }
        return userService.updatePassword(userId, request);
    }
}