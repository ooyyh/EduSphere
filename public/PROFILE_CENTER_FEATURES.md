# 个人中心功能实现

## 🎯 功能概述

实现了一个完整的个人中心功能，用户可以查看和修改自己的个人信息，包括头像上传、基本信息修改和密码修改。

## ✨ 功能特性

### 1. 个人信息管理
- **查看个人信息** - 用户名、邮箱、角色、注册时间
- **修改基本信息** - 用户名、邮箱
- **头像上传** - 支持图片上传和预览，base64存储
- **实时更新** - 修改后立即更新本地存储和显示

### 2. 密码管理
- **修改密码** - 旧密码验证 + 新密码设置
- **安全验证** - 必须输入正确的旧密码
- **密码确认** - 两次输入新密码必须一致

### 3. 用户体验
- **响应式设计** - 适配不同屏幕尺寸
- **表单验证** - 实时验证用户输入
- **加载状态** - 操作过程中显示加载动画
- **错误处理** - 友好的错误提示信息

## 🛠️ 技术实现

### 前端技术
- **Vue 3** - 组合式API
- **Element Plus** - UI组件库
- **Vue Router** - 路由管理
- **Axios** - HTTP请求
- **FileReader** - 图片文件处理

### 后端技术
- **Spring Boot** - 后端框架
- **MyBatis-Plus** - ORM框架
- **BCrypt** - 密码加密
- **JWT** - 身份认证

## 📁 文件结构

### 前端文件
```
EduSphereF/src/
├── views/pages/Profile.vue          # 个人中心页面
├── components/Header.vue            # 头部组件（添加个人中心链接）
├── router/index.js                  # 路由配置
└── utils/auth.js                    # 认证工具类
```

### 后端文件
```
EduSphereB/src/main/java/top/ooyyh/edusphere/
├── controller/UserController.java           # 用户控制器
├── service/UserService.java                 # 用户服务接口
├── service/impl/UserServiceImpl.java        # 用户服务实现
├── mapper/UserMapper.java                   # 用户数据访问接口
├── request/UserUpdateRequest.java           # 用户更新请求DTO
├── request/PasswordUpdateRequest.java       # 密码更新请求DTO
└── resources/mapper/UserMapper.xml          # 用户数据访问映射
```

## 🔧 API接口

### 1. 获取用户信息
```
GET /user/profile
Authorization: Bearer <token>
```

**响应示例：**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@edusphere.com",
    "avatar": "data:image/jpeg;base64,/9j/4AAQ...",
    "role": "admin",
    "createdAt": "2025-09-27T10:00:00"
  }
}
```

### 2. 更新用户信息
```
PUT /user/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "new_username",
  "email": "new_email@example.com",
  "avatar": "data:image/jpeg;base64,/9j/4AAQ..."
}
```

### 3. 修改密码
```
PUT /user/password
Authorization: Bearer <token>
Content-Type: application/json

{
  "oldPassword": "old_password",
  "newPassword": "new_password"
}
```

## 🎨 界面设计

### 1. 个人信息卡片
- 头像上传区域（支持拖拽上传）
- 用户名输入框（带字符限制）
- 邮箱输入框（带格式验证）
- 角色标签（只读显示）
- 注册时间（只读显示）

### 2. 密码修改卡片
- 旧密码输入框（带显示/隐藏切换）
- 新密码输入框（带强度提示）
- 确认密码输入框（带一致性验证）

### 3. 操作按钮
- 保存修改按钮（带加载状态）
- 重置按钮（清空表单）
- 修改密码按钮（带加载状态）

## 🔒 安全特性

### 1. 身份验证
- JWT token验证
- 用户ID从token中解析
- 防止未授权访问

### 2. 数据验证
- 用户名唯一性检查
- 邮箱唯一性检查
- 密码强度验证
- 旧密码正确性验证

### 3. 密码安全
- BCrypt加密存储
- 旧密码验证
- 新密码确认

## 🚀 使用说明

### 1. 访问个人中心
1. 登录系统
2. 点击右上角用户头像
3. 选择"个人中心"

### 2. 修改个人信息
1. 在"基本信息"卡片中修改用户名或邮箱
2. 点击头像区域上传新头像
3. 点击"保存修改"按钮

### 3. 修改密码
1. 在"修改密码"卡片中输入旧密码
2. 输入新密码（6-20个字符）
3. 确认新密码
4. 点击"修改密码"按钮

## 🐛 错误处理

### 1. 前端验证
- 表单字段必填验证
- 邮箱格式验证
- 密码长度验证
- 密码一致性验证

### 2. 后端验证
- 用户名唯一性检查
- 邮箱唯一性检查
- 旧密码正确性验证
- 数据库操作异常处理

### 3. 用户提示
- 成功操作提示
- 错误信息提示
- 加载状态提示

## 📱 响应式设计

### 1. 桌面端
- 两列布局（个人信息 + 密码修改）
- 最大宽度800px居中显示
- 卡片式设计

### 2. 移动端
- 单列布局
- 全宽显示
- 触摸友好的按钮尺寸

## 🔄 数据同步

### 1. 本地存储更新
- 修改成功后立即更新localStorage
- 同步更新全局用户状态
- 头部组件实时显示新头像

### 2. 数据库更新
- 事务性更新操作
- 乐观锁防止并发冲突
- 更新时间戳记录

现在个人中心功能已经完成，用户可以方便地管理自己的个人信息了！🎉
