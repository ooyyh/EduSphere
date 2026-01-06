# 动态用户ID获取修复

## 🚨 问题描述

JWT拦截器中的`getUserIdFromToken`方法硬编码了用户ID映射，这不符合最佳实践。应该从数据库动态获取用户信息。

## 🔍 问题分析

### 硬编码问题
```java
// 问题代码：硬编码用户ID映射
private Integer getUserIdFromToken(String token) {
    String username = JwtUtils.getUsername(token);
    if ("admin".equals(username)) {
        return 1;
    } else if ("teacher1".equals(username)) {
        return 2;
    }
    // ... 更多硬编码
}
```

### 问题分析
1. **维护困难** - 每次添加新用户都需要修改代码
2. **数据不一致** - 代码中的映射可能与数据库不一致
3. **扩展性差** - 无法支持动态用户管理
4. **安全性问题** - 硬编码的用户ID可能被恶意利用

## 🛠️ 修复方案

### 1. 添加数据库依赖

#### 修改JwtInterceptor.java
```java
package top.ooyyh.edusphere.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.ooyyh.edusphere.mapper.UserMapper;
import top.ooyyh.edusphere.entity.User;
import top.ooyyh.edusphere.utils.JwtUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private UserMapper userMapper;
    
    // ... 其他代码
}
```

### 2. 修改getUserIdFromToken方法

#### 修改前（硬编码）
```java
private Integer getUserIdFromToken(String token) {
    try {
        String username = JwtUtils.getUsername(token);
        if ("admin".equals(username)) {
            return 1;
        } else if ("teacher1".equals(username)) {
            return 2;
        } else if ("teacher2".equals(username)) {
            return 16;
        } else if ("student1".equals(username)) {
            return 3;
        }
    } catch (Exception e) {
        // 如果解析失败，返回默认值
    }
    return 1; // 默认返回1
}
```

#### 修改后（数据库查询）
```java
private Integer getUserIdFromToken(String token) {
    try {
        String username = JwtUtils.getUsername(token);
        if (username != null && !username.isEmpty()) {
            // 从数据库查询用户信息
            User user = userMapper.getUserByUsername(username);
            if (user != null) {
                return user.getId();
            }
        }
    } catch (Exception e) {
        // 如果解析失败，记录日志
        System.err.println("获取用户ID失败: " + e.getMessage());
    }
    return null; // 如果查询失败，返回null
}
```

### 3. 修改preHandle方法处理null返回值

#### 修改前
```java
// 将用户信息添加到请求头中，供后续使用
request.setAttribute("username", username);
request.setAttribute("role", role);
request.setAttribute("userId", getUserIdFromToken(token));
```

#### 修改后
```java
// 获取用户ID
Integer userId = getUserIdFromToken(token);
if (userId == null) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write("{\"code\":1,\"message\":\"用户不存在\"}");
    return false;
}

// 将用户信息添加到请求头中，供后续使用
request.setAttribute("username", username);
request.setAttribute("role", role);
request.setAttribute("userId", userId);
```

## 🚀 修复效果

### 1. 动态用户管理
- 支持任意数量的用户
- 新用户注册后自动可用
- 无需修改代码

### 2. 数据一致性
- 用户ID直接从数据库获取
- 避免代码与数据库不一致
- 支持用户信息的实时更新

### 3. 更好的安全性
- 用户ID验证更加严格
- 不存在的用户会被拒绝
- 减少硬编码带来的安全风险

### 4. 更好的维护性
- 代码更加简洁
- 易于维护和扩展
- 符合最佳实践

## 🔧 技术细节

### 1. 数据库查询
```java
// 使用MyBatis查询用户信息
User user = userMapper.getUserByUsername(username);
if (user != null) {
    return user.getId();
}
```

### 2. 错误处理
```java
try {
    // 数据库查询逻辑
} catch (Exception e) {
    System.err.println("获取用户ID失败: " + e.getMessage());
    return null;
}
```

### 3. 空值检查
```java
if (userId == null) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().write("{\"code\":1,\"message\":\"用户不存在\"}");
    return false;
}
```

## ⚠️ 注意事项

### 1. 性能考虑
- 每次请求都会查询数据库
- 考虑添加缓存机制
- 监控数据库查询性能

### 2. 错误处理
- 数据库连接失败的处理
- 用户不存在的处理
- 网络异常的处理

### 3. 安全性
- 确保用户查询的安全性
- 防止SQL注入攻击
- 验证用户状态

## 🔄 常见问题

### 1. 数据库连接失败
**原因**: 数据库服务未启动或连接配置错误
**解决**: 检查数据库连接配置，确保数据库服务正常运行

### 2. 用户不存在
**原因**: 用户名在数据库中不存在
**解决**: 检查用户名是否正确，确保用户已注册

### 3. 查询性能问题
**原因**: 频繁的数据库查询
**解决**: 考虑添加Redis缓存或优化查询

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

现在用户ID获取完全基于数据库查询，更加灵活和安全！🎉
