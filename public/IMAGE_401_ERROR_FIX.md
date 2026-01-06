# 图片请求401未授权错误修复

## 问题描述

前端课程列表页面出现以下错误：
```
GET http://localhost:3000/api/placeholder/600/400 401 (Unauthorized)
```

## 问题原因

1. **JWT拦截器拦截**：图片请求`/api/placeholder/600/400`被JWT拦截器拦截
2. **路径不匹配**：`/placeholder/`路径不在JWT拦截器的排除列表中
3. **代理转发**：前端Vite代理将`/api`请求转发到后端，但后端没有处理placeholder请求

## 修复方案

### 方案1：排除图片请求路径（已实施）

#### 1.1 更新WebConfig.java
```java
.excludePathPatterns(
    "/user/login",
    "/user/register",
    "/course/**",
    "/category/**",
    "/home/**",
    "/review/course/**",
    "/placeholder/**",    // 新增
    "/static/**",         // 新增
    "/images/**"          // 新增
);
```

#### 1.2 更新JwtInterceptor.java
```java
private boolean isPublicPath(String requestURI) {
    return requestURI.startsWith("/user/login") ||
           requestURI.startsWith("/user/register") ||
           requestURI.startsWith("/course/") ||
           requestURI.startsWith("/category/") ||
           requestURI.startsWith("/home/") ||
           requestURI.startsWith("/review/course/") ||
           requestURI.startsWith("/placeholder/") ||  // 新增
           requestURI.startsWith("/static/") ||       // 新增
           requestURI.startsWith("/images/");         // 新增
}
```

### 方案2：使用真实图片URL（已实施）

#### 2.1 更新测试数据
将placeholder图片URL替换为真实的图片服务：

```sql
-- 修复前
'https://via.placeholder.com/400x200/1890ff/ffffff?text=Spring+Boot'

-- 修复后
'https://picsum.photos/400/200?random=1'
```

#### 2.2 图片服务说明
- **Picsum Photos**：提供随机图片服务
- **URL格式**：`https://picsum.photos/width/height?random=number`
- **优点**：无需认证，稳定可靠，图片质量好

## 修复文件列表

1. **EduSphereB/src/main/java/top/ooyyh/edusphere/config/WebConfig.java** - 排除图片路径
2. **EduSphereB/src/main/java/top/ooyyh/edusphere/config/JwtInterceptor.java** - 更新公开路径判断
3. **public/test_data.sql** - 更新图片URL

## 测试步骤

1. **重启后端服务**
   ```bash
   cd EduSphereB
   mvn spring-boot:run
   ```

2. **重启前端服务**
   ```bash
   cd EduSphereF
   npm run dev
   ```

3. **访问课程列表页面**
   - 打开浏览器访问 `http://localhost:3000/courses`
   - 检查图片是否正常显示
   - 检查控制台是否还有401错误

4. **数据库测试**
   ```sql
   -- 运行测试数据脚本
   source public/test_data.sql
   ```

## 预期结果

修复后应该能够：
- ✅ 图片正常显示，不再出现401错误
- ✅ 课程列表页面正常渲染
- ✅ 控制台无401错误信息
- ✅ 所有图片请求都能正常加载

## 其他建议

### 1. 静态资源处理
如果后续需要处理更多静态资源，可以考虑：
- 配置Spring Boot静态资源映射
- 使用CDN服务
- 本地存储图片文件

### 2. 图片优化
- 使用WebP格式减少文件大小
- 实现图片懒加载
- 添加图片加载失败的回退机制

### 3. 安全考虑
- 验证图片URL的安全性
- 防止恶意图片请求
- 实现图片访问权限控制
