# Spring Security依赖移除修复

## 🚨 问题描述

在实现个人中心功能时，错误地引入了Spring Security的`BCryptPasswordEncoder`依赖，但项目本身没有配置Spring Security，导致依赖注入失败。

## 🔍 问题分析

### 错误代码
```java
@Autowired
private BCryptPasswordEncoder passwordEncoder; // 错误：项目中没有Spring Security配置
```

### 问题原因
1. **依赖缺失** - 项目中没有Spring Security依赖
2. **配置缺失** - 没有Spring Security配置类
3. **Bean缺失** - 没有BCryptPasswordEncoder的Bean定义

## 🛠️ 修复方案

### 1. 创建自定义密码加密工具类

#### PasswordUtils.java
```java
public class PasswordUtils {
    // 使用SHA-256 + Salt的方式加密密码
    public static String hashPassword(String password) {
        String salt = generateSalt();
        String hashedPassword = encryptPassword(password, salt);
        return salt + "$" + hashedPassword; // 格式：salt$hashedPassword
    }
    
    public static boolean verifyPassword(String password, String fullHashedPassword) {
        // 验证密码逻辑
    }
}
```

### 2. 更新UserServiceImpl

#### 修复前（错误）
```java
@Autowired
private BCryptPasswordEncoder passwordEncoder;

// 验证旧密码
if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
    return Result.error("旧密码错误");
}

// 更新密码
String encodedPassword = passwordEncoder.encode(request.getNewPassword());
```

#### 修复后（正确）
```java
// 验证旧密码
if (!PasswordUtils.verifyPassword(request.getOldPassword(), user.getPassword())) {
    return Result.error("旧密码错误");
}

// 更新密码
String encodedPassword = PasswordUtils.hashPassword(request.getNewPassword());
```

### 3. 创建完整的认证系统

#### AuthController.java
```java
@RestController
@RequestMapping("/user")
public class AuthController {
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody UserLoginRequest request) {
        // 登录逻辑，使用PasswordUtils验证密码
    }
    
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterRequest request) {
        // 注册逻辑，使用PasswordUtils加密密码
    }
}
```

## 📁 修改的文件

### 新增文件
1. **PasswordUtils.java** - 自定义密码加密工具类
2. **AuthController.java** - 认证控制器

### 修改文件
1. **UserServiceImpl.java** - 移除Spring Security依赖，使用自定义工具
2. **UserRegisterRequest.java** - 添加createdAt字段
3. **UserMapper.xml** - 更新注册SQL使用createdAt参数

## 🔒 密码加密方案

### 1. 加密算法
- **算法**: SHA-256
- **盐值**: 16字节随机盐值
- **存储格式**: `salt$hashedPassword`

### 2. 加密流程
```java
// 注册时
String hashedPassword = PasswordUtils.hashPassword("userPassword");
// 结果: "abc123$def456..." (salt$hash)

// 登录时
boolean isValid = PasswordUtils.verifyPassword("userPassword", "abc123$def456...");
```

### 3. 安全特性
- **随机盐值** - 每次加密都生成不同的盐值
- **不可逆** - SHA-256哈希算法
- **防彩虹表** - 盐值增加破解难度
- **一致性** - 相同密码每次加密结果不同，但验证都通过

## 🚀 优势对比

### Spring Security BCrypt
- ✅ 工业级安全
- ✅ 自动盐值管理
- ❌ 需要额外依赖
- ❌ 配置复杂

### 自定义PasswordUtils
- ✅ 无额外依赖
- ✅ 简单易用
- ✅ 完全控制
- ✅ 轻量级

## 🧪 测试验证

### 1. 密码加密测试
```java
String password = "test123";
String hashed = PasswordUtils.hashPassword(password);
boolean isValid = PasswordUtils.verifyPassword(password, hashed);
// 应该返回true
```

### 2. 不同密码测试
```java
String password1 = "test123";
String password2 = "test456";
String hashed1 = PasswordUtils.hashPassword(password1);
boolean isValid1 = PasswordUtils.verifyPassword(password2, hashed1);
// 应该返回false
```

### 3. 相同密码不同盐值测试
```java
String password = "test123";
String hashed1 = PasswordUtils.hashPassword(password);
String hashed2 = PasswordUtils.hashPassword(password);
boolean isValid1 = PasswordUtils.verifyPassword(password, hashed1);
boolean isValid2 = PasswordUtils.verifyPassword(password, hashed2);
// hashed1 != hashed2, 但isValid1和isValid2都应该为true
```

## 🔧 使用说明

### 1. 注册用户
```java
// 前端发送注册请求
{
  "username": "testuser",
  "password": "password123",
  "role": "student"
}

// 后端处理
String hashedPassword = PasswordUtils.hashPassword(request.getPassword());
// 存储到数据库
```

### 2. 用户登录
```java
// 前端发送登录请求
{
  "username": "testuser",
  "password": "password123",
  "role": "student"
}

// 后端验证
User user = userMapper.getUserByNamePassRole(request);
boolean isValid = PasswordUtils.verifyPassword(request.getPassword(), user.getPassword());
```

### 3. 修改密码
```java
// 验证旧密码
if (!PasswordUtils.verifyPassword(oldPassword, user.getPassword())) {
    return Result.error("旧密码错误");
}

// 加密新密码
String newHashedPassword = PasswordUtils.hashPassword(newPassword);
user.setPassword(newHashedPassword);
```

现在项目不再依赖Spring Security，使用自定义的密码加密工具，更加轻量级和可控！🎉
