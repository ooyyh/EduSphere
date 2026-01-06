# 登录401错误修复

## 🚨 问题描述

前端登录时出现401错误：
```
POST http://localhost:3000/api/user/login 401 (Unauthorized)
```

## 🔍 问题分析

### 根本原因
1. **端口配置错误** - 前端代理到3000端口，但后端在8080端口
2. **API路径不匹配** - 前端请求 `/api/user/login`，但后端接口是 `/user/login`
3. **端口冲突** - 前端和后端都使用3000端口

### 错误配置
```javascript
// 错误的vite.config.js配置
server: {
  port: 3000,  // 与后端冲突
  proxy: {
    '/api': {
      target: 'http://localhost:3000',  // 错误：后端在8080端口
      changeOrigin: true
    }
  }
}
```

## 🛠️ 修复方案

### 1. 修复前端代理配置

#### 修复前
```javascript
server: {
  port: 3000,  // 与后端冲突
  proxy: {
    '/api': {
      target: 'http://localhost:3000',  // 错误端口
      changeOrigin: true
    }
  }
}
```

#### 修复后
```javascript
server: {
  port: 5173,  // 前端使用5173端口
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 后端在8080端口
      changeOrigin: true
    }
  }
}
```

### 2. 修复后端API路径

#### 添加API前缀
```yaml
# application.yml
server:
  port: 8080
  servlet:
    context-path: /api  # 添加API前缀
```

#### 修复后的接口路径
- **前端请求**: `http://localhost:5173/api/user/login`
- **代理转发**: `http://localhost:8080/api/user/login`
- **后端接口**: `http://localhost:8080/api/user/login` ✅

### 3. 端口分配

| 服务 | 端口 | 说明 |
|------|------|------|
| 前端开发服务器 | 5173 | Vite开发服务器 |
| 后端Spring Boot | 8080 | 后端API服务 |
| 数据库MySQL | 3306 | 数据库服务 |

## 🚀 使用方法

### 1. 启动后端服务
```bash
cd EduSphereB
mvn spring-boot:run
```
后端将在 `http://localhost:8080` 启动，API路径为 `/api`

### 2. 启动前端服务
```bash
cd EduSphereF
npm run dev
```
前端将在 `http://localhost:5173` 启动

### 3. 访问应用
- 前端地址：`http://localhost:5173`
- 后端API：`http://localhost:8080/api`

## 🔧 配置详解

### 1. 前端代理配置
```javascript
// vite.config.js
server: {
  port: 5173,                    // 前端端口
  proxy: {
    '/api': {                    // 代理路径
      target: 'http://localhost:8080',  // 后端地址
      changeOrigin: true         // 改变请求头中的origin
    }
  }
}
```

### 2. 后端API配置
```yaml
# application.yml
server:
  port: 8080                     # 后端端口
  servlet:
    context-path: /api           # API前缀
```

### 3. 请求流程
1. 前端发送：`http://localhost:5173/api/user/login`
2. Vite代理转发：`http://localhost:8080/api/user/login`
3. 后端处理：`/api/user/login` → `UserController.login()`
4. 返回响应给前端

## ⚠️ 注意事项

### 1. 端口冲突
- 确保前端和后端使用不同端口
- 前端：5173，后端：8080
- 避免端口冲突导致服务无法启动

### 2. API路径一致性
- 前端请求路径：`/api/user/login`
- 后端接口路径：`/api/user/login`
- 确保路径完全匹配

### 3. 代理配置
- 不要移除API路径前缀
- 保持请求路径的完整性
- 确保代理目标正确

## 🔄 常见问题

### 1. 401 Unauthorized
**原因**: 接口路径不匹配或后端服务未启动
**解决**: 检查API路径配置，确保后端服务正常运行

### 2. 404 Not Found
**原因**: 代理配置错误或后端接口不存在
**解决**: 检查代理配置，确保后端接口存在

### 3. 端口冲突
**原因**: 前端和后端使用相同端口
**解决**: 修改端口配置，确保端口不冲突

## 🧪 测试验证

### 1. 检查后端服务
```bash
curl http://localhost:8080/api/user/login
```

### 2. 检查前端代理
```bash
curl http://localhost:5173/api/user/login
```

### 3. 检查登录接口
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456","role":"student"}'
```

现在登录接口应该可以正常工作了！🎉

