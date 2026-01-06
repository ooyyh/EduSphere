# 简化的个人中心功能

## 🎯 功能概述

实现了一个**极简**的个人中心功能，只包含基本的个人信息修改，不涉及复杂的登录注册逻辑。

## ✨ 功能特性

### 1. 个人信息管理
- **查看个人信息** - 用户名、邮箱、角色、注册时间
- **修改基本信息** - 用户名、邮箱
- **头像上传** - 支持图片上传和预览，base64存储
- **修改密码** - 简单的密码修改（不加密）

### 2. 极简设计
- **不修改现有登录注册** - 保持原有逻辑不变
- **简单密码验证** - 直接字符串比较，不加密
- **最小化依赖** - 不引入复杂的加密库

## 🛠️ 技术实现

### 前端
- **Profile.vue** - 个人中心页面
- **Header.vue** - 添加个人中心链接
- **路由配置** - 添加/profile路由

### 后端
- **UserController** - 个人中心API（/user/profile, /user/password）
- **UserService** - 简单的用户信息管理
- **UserMapper** - 基本的数据库操作

## 📁 文件结构

### 新增文件
```
EduSphereF/src/views/pages/Profile.vue          # 个人中心页面
EduSphereB/src/main/java/top/ooyyh/edusphere/
├── controller/UserController.java               # 个人中心控制器
├── service/UserService.java                     # 用户服务接口
├── service/impl/UserServiceImpl.java            # 用户服务实现
├── request/UserUpdateRequest.java               # 用户更新请求
├── request/PasswordUpdateRequest.java           # 密码更新请求
└── resources/mapper/UserMapper.xml              # 用户数据访问映射
```

### 修改文件
```
EduSphereF/src/components/Header.vue             # 添加个人中心链接
EduSphereF/src/router/index.js                   # 添加路由
EduSphereF/src/utils/auth.js                     # 添加用户信息更新方法
```

## 🔧 API接口

### 1. 获取用户信息
```
GET /user/profile
Authorization: Bearer <token>
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
- 头像上传区域
- 用户名输入框
- 邮箱输入框
- 角色标签（只读）
- 注册时间（只读）

### 2. 密码修改卡片
- 旧密码输入框
- 新密码输入框
- 确认密码输入框

## 🔒 安全说明

### 1. 密码存储
- **当前实现**: 明文存储（简单但不安全）
- **适用场景**: 开发测试环境
- **生产环境**: 建议添加密码加密

### 2. 身份验证
- **JWT验证**: 使用现有的JWT token验证
- **用户ID**: 从JWT中解析用户ID
- **权限控制**: 只能修改自己的信息

## 🚀 使用方法

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
2. 输入新密码
3. 确认新密码
4. 点击"修改密码"按钮

## ⚠️ 注意事项

### 1. 密码安全
- 当前密码以明文存储，仅适用于开发环境
- 生产环境建议添加密码加密

### 2. 数据验证
- 前端有基本的表单验证
- 后端有简单的数据验证
- 用户名和邮箱唯一性检查

### 3. 兼容性
- 不修改现有的登录注册逻辑
- 保持与现有系统的兼容性

## 🔄 后续优化

### 1. 密码加密（可选）
```java
// 如果需要密码加密，可以添加简单的MD5加密
String encryptedPassword = DigestUtils.md5Hex(password);
```

### 2. 数据验证增强
```java
// 添加更严格的数据验证
if (username.length() < 3 || username.length() > 50) {
    return Result.error("用户名长度必须在3-50个字符之间");
}
```

### 3. 头像存储优化
```java
// 可以考虑将头像存储到文件系统或云存储
// 而不是直接存储在数据库中
```

现在个人中心功能已经简化完成，不会影响现有的登录注册功能！🎉
