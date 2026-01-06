# 登录接口修复

## 🚨 问题描述

前端调用 `POST /api/user/login` 返回 401 错误，原因是后端缺少登录接口。

## 🔍 问题分析

### 错误信息
```
POST http://localhost:3000/api/user/login 401 (Unauthorized)
```

### 根本原因
- 项目中有登录相关的代码（UserLoginRequest、UserMapper等）
- 但缺少实际的登录接口实现
- 前端无法完成登录流程

## 🛠️ 修复方案

### 1. 添加登录接口

#### UserController.java
```java
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
```

### 2. 添加登录服务方法

#### UserService.java
```java
/**
 * 用户登录
 */
User login(UserLoginRequest request);
```

#### UserServiceImpl.java
```java
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
```

## 📋 API接口详情

### 登录接口
- **URL**: `POST /api/user/login`
- **Content-Type**: `application/json`
- **请求体**:
```json
{
  "username": "用户名",
  "password": "密码",
  "role": "角色"
}
```

### 响应格式
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "JWT_TOKEN_STRING",
    "user": {
      "id": 1,
      "username": "用户名",
      "email": "邮箱",
      "avatar": "头像URL",
      "role": "角色"
    }
  }
}
```

## 🔧 技术实现

### 1. 依赖现有组件
- **UserLoginRequest** - 登录请求实体（已存在）
- **UserMapper.getUserByNamePassRole()** - 数据库查询方法（已存在）
- **JwtUtils.generateToken()** - JWT生成工具（已存在）
- **Result** - 统一响应格式（已存在）

### 2. 简单密码验证
- 使用现有的数据库查询方法
- 直接比较密码字符串（不加密）
- 适合开发测试环境

### 3. JWT Token生成
- 使用现有的JwtUtils工具
- 包含用户名和角色信息
- 用于后续接口的身份验证

## 🚀 使用方法

### 1. 启动后端服务
```bash
cd EduSphereB
mvn spring-boot:run
```

### 2. 测试登录接口
```bash
curl -X POST http://localhost:3000/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "password": "123456",
    "role": "student"
  }'
```

### 3. 前端登录
- 前端登录页面现在可以正常调用登录接口
- 登录成功后会返回JWT token和用户信息
- 前端可以将token存储到localStorage中

## ⚠️ 注意事项

### 1. 密码安全
- 当前使用明文密码比较
- 仅适用于开发测试环境
- 生产环境建议添加密码加密

### 2. 角色验证
- 登录时需要指定角色
- 前端需要根据角色显示不同的界面
- 后端会根据角色进行权限控制

### 3. 错误处理
- 用户名或密码错误返回相应错误信息
- 系统异常返回通用错误信息
- 前端需要根据错误码进行相应处理

## 🔄 后续优化

### 1. 密码加密（可选）
```java
// 如果需要密码加密，可以添加简单的MD5加密
String encryptedPassword = DigestUtils.md5Hex(password);
```

### 2. 登录日志
```java
// 记录登录日志
log.info("用户登录: username={}, role={}, ip={}", 
    request.getUsername(), request.getRole(), getClientIP(request));
```

### 3. 登录限制
```java
// 添加登录失败次数限制
// 添加验证码验证
// 添加登录时间限制
```

现在登录接口已经修复，前端可以正常登录了！🎉
