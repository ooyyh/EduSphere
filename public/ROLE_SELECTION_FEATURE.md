# 角色选择功能实现说明

## 功能概述 ✅

为登录和注册页面添加了角色选择功能，用户可以选择以不同身份登录或注册。

## 实现的功能

### 1. 登录页面角色选择 ✅

**功能描述**：
- 用户可以选择以学生、教师或管理员身份登录
- 支持所有三种角色：`student`, `teacher`, `admin`
- 添加了表单验证，确保必须选择角色

**实现代码**：
```vue
<el-form-item label="登录身份" prop="role">
  <el-select v-model="loginForm.role" placeholder="请选择登录身份" style="width: 100%">
    <el-option 
      v-for="(label, value) in ROLE_NAMES" 
      :key="value" 
      :label="label" 
      :value="value" 
    />
  </el-select>
</el-form-item>
```

### 2. 注册页面角色选择 ✅

**功能描述**：
- 用户可以选择以学生或教师身份注册
- 不允许普通用户注册为管理员（安全考虑）
- 只显示 `student` 和 `teacher` 选项

**实现代码**：
```vue
<el-form-item label="注册身份" prop="role">
  <el-select v-model="registerForm.role" placeholder="请选择注册身份" style="width: 100%">
    <el-option 
      v-for="(label, value) in ROLE_NAMES" 
      :key="value" 
      :label="label" 
      :value="value"
      v-if="value !== 'admin'"
    />
  </el-select>
</el-form-item>
```

### 3. 角色常量配置 ✅

**配置文件**：`src/utils/constants.js`

```javascript
// 角色常量定义
export const ROLES = {
  ADMIN: 'admin',
  TEACHER: 'teacher', 
  STUDENT: 'student'
}

// 角色显示名称映射
export const ROLE_NAMES = {
  [ROLES.ADMIN]: '管理员',
  [ROLES.TEACHER]: '教师',
  [ROLES.STUDENT]: '学生'
}
```

### 4. 表单验证 ✅

**登录页面验证**：
```javascript
role: [
  { required: true, message: '请选择登录身份', trigger: 'change' }
]
```

**注册页面验证**：
```javascript
role: [
  { required: true, message: '请选择注册身份', trigger: 'change' }
]
```

## 角色权限说明

### 1. 学生 (student)
- **权限**：浏览课程、购买课程、学习课程、评价课程
- **功能**：个人中心、我的课程、购物车、订单管理

### 2. 教师 (teacher)  
- **权限**：创建课程、管理课程、查看学生、收入统计
- **功能**：课程管理、学生管理、收入统计

### 3. 管理员 (admin)
- **权限**：系统管理、用户管理、课程审核、数据统计
- **功能**：用户管理、课程审核、系统设置、数据统计

## 安全考虑

### 1. 注册限制
- 普通用户无法注册为管理员
- 管理员账号只能由系统创建
- 防止权限提升攻击

### 2. 登录验证
- 后端验证用户角色与登录角色是否匹配
- 防止角色冒充攻击
- JWT token包含角色信息

## 使用流程

### 1. 用户注册流程
```
1. 填写用户名和密码
2. 选择注册身份（学生/教师）
3. 提交注册请求
4. 后端创建对应角色的用户账号
```

### 2. 用户登录流程
```
1. 输入用户名和密码
2. 选择登录身份（学生/教师/管理员）
3. 提交登录请求
4. 后端验证身份和角色
5. 返回JWT token和用户信息
```

## 界面展示

### 登录页面
- ✅ 用户名/邮箱输入框
- ✅ 密码输入框
- ✅ **角色选择下拉框**（新增）
- ✅ 记住我选项
- ✅ 登录按钮

### 注册页面
- ✅ 用户名输入框
- ✅ 密码输入框
- ✅ 确认密码输入框
- ✅ **角色选择下拉框**（新增）
- ✅ 注册按钮

## 技术实现

### 1. 前端实现
- 使用Element Plus的`el-select`组件
- 动态渲染角色选项
- 表单验证集成
- 响应式数据绑定

### 2. 后端支持
- 数据库role字段支持字符串类型
- 用户认证时验证角色匹配
- JWT token包含角色信息
- 权限控制基于角色

### 3. 数据流
```
前端选择角色 → 发送到后端 → 后端验证角色 → 返回对应权限的token
```

## 测试建议

### 1. 功能测试
- 测试不同角色的登录
- 验证角色选择是否生效
- 检查表单验证是否正常

### 2. 安全测试
- 测试角色权限控制
- 验证管理员注册限制
- 检查JWT token中的角色信息

### 3. 用户体验测试
- 界面是否友好
- 操作是否流畅
- 错误提示是否清晰

## 后续优化

### 1. 界面优化
- 添加角色图标
- 优化选择器样式
- 添加角色说明

### 2. 功能扩展
- 角色切换功能
- 多角色支持
- 角色权限细化

### 3. 安全增强
- 角色验证加强
- 权限审计日志
- 异常登录检测
