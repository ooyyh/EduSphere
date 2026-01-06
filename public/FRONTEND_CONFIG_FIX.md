# 前端配置修复

## 🚨 问题描述

前端出现以下错误：
1. `GET http://localhost:3000/src/stores/auth 404 (Not Found)`
2. `Failed to fetch dynamically imported module: http://localhost:3000/src/views/pages/Profile.vue`

## 🔍 问题分析

### 根本原因
前端开发服务器配置错误：
1. **代理配置错误** - 前端代理到 `http://localhost:8080`，但后端运行在 `http://localhost:3000`
2. **路径重写错误** - 将 `/api` 重写为空，但后端接口需要 `/api` 前缀
3. **端口冲突** - 前端和后端都使用3000端口

### 错误配置
```javascript
// 错误的配置
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 错误：后端在3000端口
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')  // 错误：移除了/api前缀
    }
  }
}
```

## 🛠️ 修复方案

### 1. 修复vite.config.js

#### 修复前
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
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
      target: 'http://localhost:3000',  // 后端在3000端口
      changeOrigin: true
      // 不移除/api前缀，保持完整路径
    }
  }
}
```

### 2. 端口分配

| 服务 | 端口 | 说明 |
|------|------|------|
| 前端开发服务器 | 5173 | Vite默认端口 |
| 后端Spring Boot | 3000 | 后端API服务 |
| 数据库MySQL | 3306 | 数据库服务 |

### 3. 代理配置说明

#### 前端请求流程
1. 前端发送请求：`http://localhost:5173/api/user/login`
2. Vite代理转发到：`http://localhost:3000/api/user/login`
3. 后端处理请求并返回响应
4. 前端接收响应

#### 静态资源请求
1. 前端请求：`http://localhost:5173/src/views/pages/Profile.vue`
2. Vite直接处理，不经过代理
3. 返回Vue组件文件

## 🚀 使用方法

### 1. 启动后端服务
```bash
cd EduSphereB
mvn spring-boot:run
```
后端将在 `http://localhost:3000` 启动

### 2. 启动前端服务
```bash
cd EduSphereF
npm run dev
```
前端将在 `http://localhost:5173` 启动

### 3. 访问应用
- 前端地址：`http://localhost:5173`
- 后端API：`http://localhost:3000/api`

## 🔧 配置详解

### 1. Vite代理配置
```javascript
server: {
  port: 5173,                    // 前端端口
  proxy: {
    '/api': {                    // 代理路径
      target: 'http://localhost:3000',  // 目标服务器
      changeOrigin: true         // 改变请求头中的origin
    }
  }
}
```

### 2. 请求路径映射
| 前端请求 | 实际请求 | 说明 |
|----------|----------|------|
| `/api/user/login` | `http://localhost:3000/api/user/login` | API请求 |
| `/src/views/pages/Profile.vue` | 本地文件 | 静态资源 |
| `/assets/logo.png` | 本地文件 | 静态资源 |

### 3. 环境变量配置
```javascript
// .env.development
VITE_API_BASE_URL=http://localhost:3000/api
VITE_APP_TITLE=EduSphere
```

## ⚠️ 注意事项

### 1. 端口冲突
- 确保前端和后端使用不同端口
- 前端：5173，后端：3000
- 避免端口冲突导致服务无法启动

### 2. 代理配置
- 不要移除API路径前缀
- 保持请求路径的完整性
- 确保代理目标正确

### 3. 跨域问题
- 使用代理解决跨域问题
- 设置 `changeOrigin: true`
- 确保请求头正确传递

## 🔄 常见问题

### 1. 404错误
**问题**: `GET http://localhost:3000/src/stores/auth 404`
**原因**: 前端资源请求到了后端服务器
**解决**: 检查代理配置，确保静态资源不经过代理

### 2. 动态导入失败
**问题**: `Failed to fetch dynamically imported module`
**原因**: 文件路径错误或服务器配置问题
**解决**: 检查文件路径，确保开发服务器正常运行

### 3. API请求失败
**问题**: API请求返回404或500错误
**原因**: 代理配置错误或后端服务未启动
**解决**: 检查代理配置，确保后端服务正常运行

## 🧪 测试验证

### 1. 检查前端服务
```bash
curl http://localhost:5173
```

### 2. 检查后端服务
```bash
curl http://localhost:3000/api/home/data
```

### 3. 检查代理配置
```bash
curl http://localhost:5173/api/home/data
```

现在前端配置已经修复，应该可以正常访问个人中心页面了！🎉
