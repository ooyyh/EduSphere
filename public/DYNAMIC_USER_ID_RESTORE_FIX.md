# 动态用户ID查询恢复修复

## 🚨 问题描述

JWT拦截器工作正常，但个人主页显示的是硬编码的用户信息（admin），而不是当前登录用户的真实信息。

## 🔍 问题分析

### 根本原因
**JWT拦截器中的用户ID查询被简化了**：
- 之前为了调试，将`getUserIdFromToken`方法简化为直接返回1
- 所有用户都被映射到用户ID 1（admin）
- 个人主页显示的是admin的信息，而不是当前登录用户的信息

### 问题流程
1. 用户登录: student2
2. JWT拦截器解析token: 获取用户名student2
3. 查询用户ID: 直接返回硬编码的1
4. 个人主页查询: 查询ID为1的用户信息（admin）
5. 显示结果: admin的信息，而不是student2的信息

## 🛠️ 修复方案

### 1. 恢复数据库查询

#### 修改前（硬编码）
```java
private Integer getUserIdFromToken(String token) {
    try {
        String username = JwtUtils.getUsername(token);
        if (username != null && !username.isEmpty()) {
            // 暂时简化处理，直接返回1
            System.out.println("JWT拦截器 - 用户名: " + username);
            return 1;  // ❌ 硬编码返回1
        }
    } catch (Exception e) {
        System.err.println("获取用户ID失败: " + e.getMessage());
    }
    return null;
}
```

#### 修改后（数据库查询）
```java
private Integer getUserIdFromToken(String token) {
    try {
        String username = JwtUtils.getUsername(token);
        if (username != null && !username.isEmpty()) {
            System.out.println("JWT拦截器 - 用户名: " + username);
            // 从数据库查询用户信息
            User user = userMapper.getUserByUsername(username);
            if (user != null) {
                System.out.println("JWT拦截器 - 查询到用户ID: " + user.getId());
                return user.getId();  // ✅ 返回真实的用户ID
            } else {
                System.out.println("JWT拦截器 - 用户不存在: " + username);
            }
        }
    } catch (Exception e) {
        System.err.println("获取用户ID失败: " + e.getMessage());
        e.printStackTrace();
    }
    return null;
}
```

### 2. 添加详细调试信息

#### 调试日志
```java
System.out.println("JWT拦截器 - 用户名: " + username);
System.out.println("JWT拦截器 - 查询到用户ID: " + user.getId());
System.out.println("JWT拦截器 - 用户不存在: " + username);
```

#### 异常处理
```java
catch (Exception e) {
    System.err.println("获取用户ID失败: " + e.getMessage());
    e.printStackTrace();  // 打印完整异常堆栈
}
```

## 🚀 修复效果

### 1. 用户信息正确
- 每个用户显示自己的信息
- student2登录显示student2的信息
- admin登录显示admin的信息

### 2. 动态用户管理
- 支持任意数量的用户
- 新用户注册后自动可用
- 用户信息实时更新

### 3. 调试信息完整
- 可以清楚看到用户名和用户ID
- 可以验证数据库查询结果
- 便于排查问题

## 🔧 技术细节

### 1. 数据库查询
```java
// 根据用户名查询用户信息
User user = userMapper.getUserByUsername(username);
if (user != null) {
    return user.getId();  // 返回真实的用户ID
}
```

### 2. 错误处理
```java
try {
    // 数据库查询逻辑
} catch (Exception e) {
    System.err.println("获取用户ID失败: " + e.getMessage());
    e.printStackTrace();
    return null;
}
```

### 3. 调试信息
```java
System.out.println("JWT拦截器 - 用户名: " + username);
System.out.println("JWT拦截器 - 查询到用户ID: " + user.getId());
```

## ⚠️ 注意事项

### 1. 数据库连接
- 确保数据库连接正常
- 检查UserMapper是否正确注入
- 验证SQL查询是否正确

### 2. 性能考虑
- 每次请求都会查询数据库
- 考虑添加缓存机制
- 监控数据库查询性能

### 3. 错误处理
- 数据库连接失败的处理
- 用户不存在的处理
- 网络异常的处理

## 🔄 常见问题

### 1. 用户信息不正确
**原因**: 数据库查询失败或用户ID映射错误
**解决**: 检查数据库连接和用户ID映射

### 2. 数据库查询失败
**原因**: 数据库连接问题或SQL错误
**解决**: 检查数据库连接和SQL语句

### 3. 用户不存在
**原因**: 用户名在数据库中不存在
**解决**: 检查用户名是否正确，确保用户已注册

## 🎯 最佳实践

### 1. 缓存机制
```java
// 可以考虑添加缓存
@Cacheable(value = "user", key = "#username")
public User getUserByUsername(String username) {
    return userMapper.getUserByUsername(username);
}
```

### 2. 异常处理
```java
// 更详细的异常处理
try {
    User user = userMapper.getUserByUsername(username);
    if (user == null) {
        log.warn("用户不存在: {}", username);
        return null;
    }
    return user.getId();
} catch (DataAccessException e) {
    log.error("数据库查询失败: {}", e.getMessage());
    return null;
}
```

### 3. 性能监控
```java
// 添加性能监控
@Timed(name = "user.query", description = "用户查询时间")
public User getUserByUsername(String username) {
    return userMapper.getUserByUsername(username);
}
```

现在每个用户都会显示自己的真实信息了！🎉
